package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.job.Job;
import edu.asu.ying.mapreduce.mapreduce.job.JobSchedulingResult;
import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskCompletion;
import edu.asu.ying.mapreduce.mapreduce.task.TaskSchedulingResult;
import edu.asu.ying.p2p.RemotePeer;

/**
 * Provides the implementation of {@code RemoteScheduler} which will be accessible to remote peers
 * when exported. The proxy implementation glues the remote scheduler proxy to the concrete local
 * scheduler implementation while implementing the appropriate patterns to be RMI-compatible.
 */
public final class RemoteSchedulerProxy implements RemoteScheduler {

  public static RemoteScheduler createProxyTo(final LocalScheduler scheduler) {
    return new RemoteSchedulerProxy(scheduler);
  }


  private final LocalScheduler localScheduler;

  private RemoteSchedulerProxy(final LocalScheduler scheduler) {
    this.localScheduler = scheduler;
  }

  @Override
  public JobSchedulingResult acceptJobAsResponsibleNode(final Job job) throws RemoteException {
    return this.localScheduler.acceptJobAsResponsibleNode(job);
  }

  @Override
  public TaskSchedulingResult acceptInitialTask(final Task task) throws RemoteException {
    return this.localScheduler.acceptInitialTask(task);
  }

  @Override
  public TaskSchedulingResult acceptTask(final Task task) throws RemoteException {
    return this.localScheduler.acceptTask(task);
  }

  @Override
  public void reduceTaskCompletion(final TaskCompletion completion) throws RemoteException {
    this.localScheduler.reduceTaskCompletion(completion);
  }

  @Override
  public int getBackpressure() throws RemoteException {
    return this.localScheduler.getForwardQueue().size();
  }

  @Override
  public RemotePeer getNode() throws RemoteException {
    return this.localScheduler.getLocalPeer().getProxy();
  }
}

