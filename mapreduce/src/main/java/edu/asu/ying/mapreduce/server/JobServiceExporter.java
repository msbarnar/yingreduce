package edu.asu.ying.mapreduce.server;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import javax.inject.Inject;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.mapreduce.job.Job;
import edu.asu.ying.mapreduce.job.JobException;
import edu.asu.ying.mapreduce.job.JobService;

public final class JobServiceExporter
    implements Exporter<JobService, RemoteJobService>, RemoteJobService {

  private final Activator activator;
  private JobService service;

  @Inject
  private JobServiceExporter(Activator activator) {
    this.activator = activator;
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
  public RemoteJobService export(JobService service) throws ExportException {
    this.service = service;
    return activator.bind(RemoteJobService.class, this);
  }
}
