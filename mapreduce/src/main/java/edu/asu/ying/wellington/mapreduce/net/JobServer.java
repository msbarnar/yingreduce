package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;
import edu.asu.ying.wellington.mapreduce.job.JobHistory;
import edu.asu.ying.wellington.mapreduce.job.JobService;

/**
 * The {@code JobServer} hosts the job service, accepting jobs locally and remotely and managing
 * their delegation.
 */
public final class JobServer implements JobService, WrapperFactory<JobServer, RemoteJobService> {

  private final LocalNode localNode;

  public JobServer(LocalNode localNode) {
    this.localNode = localNode;
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
      try {
        job.setResponsibleNode(responsibleNode.getIdentifier());
      } catch (RemoteException e) {
        job.getHistory().getCurrent().setAction(JobHistory.Action.ForwardFailed);
        throw new JobException("Connection to responsible node interrupted", e);
      }
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
   * Wraps the local job server for remote export.
   */
  @Override
  public RemoteJobService createWrapper(JobServer target) {
    return new JobServerWrapper(target);
  }

  /**
   * Queues the job for reducer allocation and task delegation.
   */
  private void queue(Job job) {
    job.getHistory().getCurrent().setAction(JobHistory.Action.AcceptedResponsibility);
  }

  private boolean isResponsibleFor(Job job) {
    return this.localNode.getNodeID().equals(job.getResponsibleNodeID());
  }

  /**
   * Finds the node most closely matching the job's table ID.
   */
  private RemoteNode findResponsibleNode(Job job) {
    return this.localNode.findNode(job.getTableID().forPage(0).toString());
  }

  private final class JobServerWrapper implements RemoteJobService {

    private final JobServer wrappedServer;

    private JobServerWrapper(JobServer server) {
      this.wrappedServer = server;
    }

    @Override
    public void accept(Job job) throws RemoteException {
      try {
        this.wrappedServer.accept(job);
      } catch (JobException e) {
        throw new RemoteException("Remote node failed to accept job", e);
      }
    }
  }
}
