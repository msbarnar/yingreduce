package edu.asu.ying.wellington.mapreduce.job;

import edu.asu.ying.wellington.mapreduce.JobService;

/**
 * {@code JobClient} is the local interface to the job service, facilitating the starting and
 * examining of jobs on the local system.
 */
public final class JobClient {

  private final JobService service;

  public JobClient(JobService service) {
    this.service = service;
  }

  /**
   * Creates a new {@link Job} from a {@link JobConf} and sends it to the job service.
   */
  public void runJob(JobConf jobConf) {

  }
}
