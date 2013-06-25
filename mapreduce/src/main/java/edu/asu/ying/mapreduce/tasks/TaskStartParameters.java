package edu.asu.ying.mapreduce.tasks;

/**
 * Allows fine-grained control over the starting of new tasks.
 */
public enum TaskStartParameters {
  StartImmediately;

  public static final TaskStartParameters Default = TaskStartParameters.StartImmediately;
}
