package edu.asu.ying.mapreduce.mapreduce.queuing;

import edu.asu.ying.mapreduce.mapreduce.task.Task;

/**
 * A {@code TaskQueue} watches a specific {@link java.util.Queue} and handles
 * {@link edu.asu.ying.mapreduce.mapreduce.task.Task}s as they arrive.
 */
public interface TaskQueue extends Runnable {

  /**
   * Starts the worker, waiting for tasks to arrive on the queue.
   */
  void start();

  /**
   * Returns {@code true} if the task is added to the queue, or {@code false} if the queue is full
   * or rejected the task.
   */
  boolean offer(final Task task);

  /**
   * Returns the number of elements in the underlying queue.
   */
  int size();
}
