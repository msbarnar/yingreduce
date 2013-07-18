package edu.asu.ying.mapreduce.mapreduce.task;

/**
 * Allows fine-grained control over the starting of new mapreduce.
 */
public enum TaskStartParameters {
  StartImmediately;

  public static final TaskStartParameters Default = TaskStartParameters.StartImmediately;
}
