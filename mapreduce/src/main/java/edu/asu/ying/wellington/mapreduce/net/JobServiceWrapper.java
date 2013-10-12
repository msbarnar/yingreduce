package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;
import edu.asu.ying.wellington.mapreduce.job.JobServer;

public final class JobServiceWrapper implements RemoteJobService {

  private final JobServer wrappedServer;

  public JobServiceWrapper(JobServer server, Activator activator) {
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
