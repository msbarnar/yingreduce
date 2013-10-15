package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import javax.inject.Inject;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;
import edu.asu.ying.wellington.mapreduce.job.JobService;

public final class JobServiceWrapper implements RemoteJobService, Wrapper<RemoteJobService> {

  private final JobService wrappedServer;
  private final RemoteJobService proxyInstance;

  @Inject
  private JobServiceWrapper(JobService server, Activator activator) {
    this.wrappedServer = server;
    this.proxyInstance = activator.bind(RemoteJobService.class).toInstance(this);
  }

  @Override
  public void accept(Job job) throws RemoteException {
    try {
      this.wrappedServer.accept(job);
    } catch (JobException e) {
      throw new RemoteException("Remote node failed to accept job", e);
    }
  }

  @Override
  public RemoteJobService getProxy() {
    return proxyInstance;
  }
}
