package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import javax.inject.Inject;

import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobException;
import edu.asu.ying.wellington.mapreduce.job.JobService;

public final class JobServiceWrapper
    implements RemoteJobService, Wrapper<RemoteJobService, JobService> {

  private JobService service;

  @Inject
  private JobServiceWrapper() {
  }

  @Override
  public void accept(Job job) throws RemoteException {
    try {
      this.service.accept(job);
    } catch (JobException e) {
      throw new RemoteException("Remote node failed to accept job", e);
    }
  }

  @Override
  public void wrap(JobService target) throws RemoteException {
    this.service = target;
  }
}
