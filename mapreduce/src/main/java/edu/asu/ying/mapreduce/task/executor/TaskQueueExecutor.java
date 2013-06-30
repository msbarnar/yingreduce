package edu.asu.ying.mapreduce.task.executor;

/**
 * A {@code TaskQueueExecutor} watches a specific {@link java.util.Queue} and handles
 * {@link edu.asu.ying.mapreduce.task.Task} objects as they arrive.
 */
public interface TaskQueueExecutor {

  /**
   * Causes the executor to begin listening for {@link edu.asu.ying.mapreduce.task.Task} objects
   * on its given queue.
   */
  void start();
}
