package edu.asu.ying.wellington.mapreduce.job;

import java.rmi.RemoteException;

import edu.asu.ying.wellington.mapreduce.job.scheduling.JobDelegator;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;
import edu.asu.ying.wellington.mapreduce.net.RemoteNode;

/**
 * The {@code JobServer} hosts the job service, accepting jobs locally and remotely and managing
 * their delegation.
 */
public final class JobServer implements JobService {

  private final LocalNode localNode;

  private final JobDelegator jobDelegator;

  public JobServer(LocalNode localNode) {
    this.localNode = localNode;
    this.jobDelegator = new JobDelegator(this.localNode);
    this.jobDelegator.start();
  }

  /**
   * If this node is responsible for {@code job}, it will be queued for delegation. Otherwise the
   * job will be forwarded to the responsible node.
   */
  @Override
  public void accept(Job job) throws JobException {
    // Add ourselves to the job's history
    job.getHistory().touch(this.localNode);
    // If we are the responsible node for the job then accept it
    if (this.isResponsibleFor(job)) {
      this.queue(job);
    } else {
      // Forward the job to the responsible node
      RemoteNode responsibleNode = this.findResponsibleNode(job);
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
    if (this.jobDelegator.offer(job)) {
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
      return this.localNode.getIdentifier().equals(job.getResponsibleNode().getIdentifier());
    } catch (RemoteException e) {
      throw new RuntimeException("Remote node is unreachable", e);
    }
  }

  /**
   * Finds the node most closely matching the job's table ID.
   */
  private RemoteNode findResponsibleNode(Job job) {
    return this.localNode.findNode(job.getTableID().forPage(0).toString());
  }
}
