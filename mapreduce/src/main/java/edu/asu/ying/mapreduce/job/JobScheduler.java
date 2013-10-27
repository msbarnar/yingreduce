package edu.asu.ying.mapreduce.job;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.rmi.Local;
import edu.asu.ying.mapreduce.server.JobServiceExporter;
import edu.asu.ying.mapreduce.server.RemoteJobService;

/**
 * The {@code JobScheduler} hosts the job service, accepting jobs locally and remotely and managing
 * their delegation.
 */
public final class JobScheduler implements JobService {

  private final RemoteJobService proxy;

  private final Provider<String> localNodeIDProvider;
  private final NodeLocator nodeLocator;

  private final QueueExecutor<Job> jobDelegator;

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
      RemoteNode responsibleNode = null;
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
    //return nodeLocator.find(PageName.firstPageOf(job.getTableName()).toString());
    // FIXME: Find a node for the job
    return null;
  }

  @Override
  public RemoteJobService asRemote() {
    return this.proxy;
  }
}
