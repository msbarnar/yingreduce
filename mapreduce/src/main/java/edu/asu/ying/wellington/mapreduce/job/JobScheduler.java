package edu.asu.ying.wellington.mapreduce.job;

import com.google.inject.Inject;
import com.google.inject.Provider;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.Path;
import edu.asu.ying.wellington.mapreduce.server.JobServiceExporter;
import edu.asu.ying.wellington.mapreduce.server.RemoteJobService;

/**
 * The {@code JobScheduler} hosts the job service, accepting jobs locally and remotely and managing
 * their delegation.
 */
public final class JobScheduler implements JobService {

  private final Logger log = Logger.getLogger(JobScheduler.class);

  private final RemoteJobService proxy;

  private final Provider<String> localNodeIDProvider;
  private final NodeLocator nodeLocator;

  private final QueueExecutor<Job> jobDelegator;

  private final Map<String, Integer> openJobs = new HashMap<>();

  @Inject
  private JobScheduler(JobServiceExporter exporter,
                       @Local Provider<String> localNodeIDProvider,
                       NodeLocator nodeLocator,
                       @Jobs QueueExecutor<Job> jobDelegator) {

    this.localNodeIDProvider = localNodeIDProvider;
    this.nodeLocator = nodeLocator;
    this.jobDelegator = jobDelegator;

    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void start() {
    jobDelegator.start();
  }

  @Override
  public void stop() {
    jobDelegator.stop();
  }

  /**
   * If this node is responsible for {@code job}, it will be queued for delegation. Otherwise the
   * job will be forwarded to the responsible node.
   */
  @Override
  public void accept(Job job) throws JobException {
    // Add ourselves to the job's history
    job.getHistory().visitedBy(localNodeIDProvider.get());
    // If we are the responsible node for the job then accept it
    if (isResponsibleFor(job)) {
      queue(job);
    } else {
      // Forward the job to the responsible node
      RemoteNode responsibleNode;
      try {
        responsibleNode = findResponsibleNode(job);
      } catch (IOException e) {
        throw new JobException("Exception finding responsible node for job", e);
      }

      job.setResponsibleNode(responsibleNode);
      try {
        responsibleNode.getJobService().accept(job);
      } catch (RemoteException e) {
        job.getHistory().getCurrent().setAction(JobHistory.Action.ForwardFailed);
        throw new JobException("Responsible node failed to accept job", e);
      }
      job.getHistory().getCurrent().setAction(JobHistory.Action.ForwardedToResponsibleNode);
    }
  }

  @Override
  public void completeReduction(RemoteNode reducer, Job job) {
    synchronized (openJobs) {
      Integer numCompletions = openJobs.get(job.getName());
      if (numCompletions == null) {
        numCompletions = 1;
      } else {
        ++numCompletions;
        log.info("Waiting for " + (job.getReducerCount() - numCompletions) + " more reductions");
        if (numCompletions >= job.getReducerCount()) {
          commitResults(job);
          openJobs.remove(job.getName());
          log.info("Job complete: " + job.getTableName());
          log.info(job.getTimeElapsed() + " ms");
          return;
        }
      }
      openJobs.put(job.getName(), numCompletions);
    }
  }

  /**
   * Queues the job for reducer allocation and task delegation.
   */
  private void queue(Job job) throws JobException {
    jobDelegator.add(job);
    job.getHistory().getCurrent().setAction(JobHistory.Action.AcceptedJob);
    job.setStatus(Job.Status.Accepted);
  }

  private boolean isResponsibleFor(Job job) {
    try {
      RemoteNode responsibleNode = job.getResponsibleNode();
      return responsibleNode != null
             && localNodeIDProvider.get().equals(responsibleNode.getName());
    } catch (RemoteException e) {
      throw new RuntimeException("Remote node is unreachable", e);
    }
  }

  /**
   * Finds the node with the first page of the job's table.
   */
  private RemoteNode findResponsibleNode(Job job) throws IOException {
    return nodeLocator.find(PageName.firstPageOf(new Path(job.getTableName())).toString());
  }

  private void commitResults(Job job) {
    for (RemoteNode node : job.getReducerNodeIDs()) {
      try {
        node.getReducerFor(null).commit();
      } catch (RemoteException e) {
        log.error("Reducer unreachable for commit", e);
      }
    }
    log.info("Reductions committed");
  }

  @Override
  public RemoteJobService asRemote() {
    return this.proxy;
  }
}
