package edu.asu.ying.mapreduce.mapreduce.execution;

/**
 * A {@code TaskQueueExecutor} watches a specific {@link java.util.Queue} and handles
 * {@link edu.asu.ying.mapreduce.mapreduce.task.Task} objects as they arrive.
 */
public interface TaskQueueExecutor extends Runnable {
}
