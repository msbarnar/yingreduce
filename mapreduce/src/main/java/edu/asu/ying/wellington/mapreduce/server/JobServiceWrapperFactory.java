package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;
import edu.asu.ying.wellington.mapreduce.job.JobService;

public final class JobServiceWrapperFactory
    implements WrapperFactory<JobService, RemoteJobService> {

  @Override
  public RemoteJobService create(JobService target, Activator activator) {
    return new JobServiceWrapper(target);
  }

  private final class JobServiceWrapper implements RemoteJobService {

    private final JobService wrappedServer;

    public JobServiceWrapper(JobService server) {
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
