package edu.asu.ying.mapreduce.task;

/**
 * Allows fine-grained control over the starting of new task.
 */
public enum TaskStartParameters {
  StartImmediately;

  public static final TaskStartParameters Default = TaskStartParameters.StartImmediately;
}
