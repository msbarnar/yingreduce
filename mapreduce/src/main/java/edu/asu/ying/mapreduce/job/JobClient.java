package edu.asu.ying.mapreduce.job;

import com.google.inject.Inject;

/**
 * {@code JobClient} is the local interface to the job service, facilitating the starting and
 * examining of jobs on the local system.
 */
public final class JobClient {

  private final JobService service;

  @Inject
  private JobClient(JobService service) {
    this.service = service;
  }

  /**
   * Creates a new {@link Job} from a {@link JobConf} and sends it to the job service.
   */
  public void runJob(JobConf jobConf) throws JobException {
    Job job = new Job(jobConf.getTableName());
    service.accept(job);
  }
}
