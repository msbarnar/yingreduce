package edu.asu.ying.wellington.mapreduce.job;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.rmi.RemoteException;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * The {@code JobScheduler} hosts the job service, accepting jobs locally and remotely and managing
 * their delegation.
 */
@Singleton
public final class JobScheduler implements JobService {

  private final LocalNode localNode;
  private final NodeLocator nodeLocator;

  private final QueueExecutor<Job> jobDelegator;

  @Inject
  private JobScheduler(LocalNode localNode, NodeLocator nodeLocator, JobDelegator jobDelegator) {
    this.localNode = localNode;
    this.nodeLocator = nodeLocator;
    this.jobDelegator = jobDelegator;
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
    job.getHistory().touchedBy(localNode);
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
    if (jobDelegator.offer(job)) {
      job.getHistory().getCurrent().setAction(JobHistory.Action.AcceptedJob);
      job.setStatus(Job.Status.Accepted);
    } else {
      job.getHistory().getCurrent().setAction(JobHistory.Action.RejectedJob);
      job.setStatus(Job.Status.Rejected);
      throw new JobException("The job delegation queue rejected the job.");
    }
  }

  private boolean isResponsibleFor(Job job) {
    try {
      return localNode.getID().equals(job.getResponsibleNode().getIdentifier());
    } catch (RemoteException e) {
      throw new RuntimeException("Remote node is unreachable", e);
    }
  }

  /**
   * Finds the node with the first page of the job's table.
   */
  // FIXME: page duplication means we have to pick a random one of the nodes that has this page
  private RemoteNode findResponsibleNode(Job job) throws IOException {
    return nodeLocator.find(PageIdentifier.firstPageOf(job.getTableID()).toString());
  }
}
