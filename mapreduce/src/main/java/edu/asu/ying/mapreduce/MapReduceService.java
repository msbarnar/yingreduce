package edu.asu.ying.mapreduce;

/**
 * {@code MapReduceService} is the main entrypoint to the services, e.g. the job service and the
 * database.
 */
public interface MapReduceService {

  JobService getJobService();

  DatabaseService getDatabaseService();
}
