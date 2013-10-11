package edu.asu.ying.mapreduce.mapreduce.scheduling;

import edu.asu.ying.mapreduce.mapreduce.job.Job;

/**
 * {@code JobDelegator} watches a {@link edu.asu.ying.mapreduce.mapreduce.job.Job} queue and
 * delegates jobs that come into it as tasks to the appropriate {@code initial} nodes.
 */
public interface JobDelegator extends Runnable {

  void start();

  boolean offer(final Job job);
}
