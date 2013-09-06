package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;

/**
 *
 */
public final class TaskCompletion implements Serializable {

  private static final long serialVersionUID = 1L;

  private final Task task;
  private final Serializable result;

  public TaskCompletion(final Task task, final Serializable result) {
    this.task = task;
    this.result = result;
  }

  public final Task getTask() {
    return task;
  }

  public final Serializable getResult() {
    return result;
  }
}
