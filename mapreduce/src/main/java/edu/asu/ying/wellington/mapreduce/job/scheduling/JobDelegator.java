package edu.asu.ying.wellington.mapreduce.job.scheduling;

/**
 * {@code JobDelegator} watches a {@link edu.asu.ying.wellington.mapreduce.job.Job} queue and
 * delegates jobs that come into it as tasks to the appropriate {@code initial} nodes.
 */
public interface JobDelegator extends Runnable {

  void start();

  boolean offer(final Job job);
}
