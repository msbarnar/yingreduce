package edu.asu.ying.mapreduce.mapreduce.job;

/**
 * {@code JobDelegator} watches a {@link Job} queue and delegates jobs that come into it as tasks
 * to the appropriate {@code initial} nodes.
 */
public interface JobDelegator extends Runnable {
}
