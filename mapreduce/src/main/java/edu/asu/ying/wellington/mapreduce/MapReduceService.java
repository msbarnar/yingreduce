package edu.asu.ying.wellington.mapreduce;

import edu.asu.ying.wellington.mapreduce.job.JobService;

/**
 * {@code MapReduceService} is the main entrypoint to the services, e.g. the job service and the
 * database.
 */
public interface MapReduceService {

  JobService getJobService();
}
