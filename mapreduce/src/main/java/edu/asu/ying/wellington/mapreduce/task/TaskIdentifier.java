package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.wellington.Identifier;

/**
 *
 */
public final class TaskIdentifier extends Identifier {

  private static final long SerialVersionUID = 1L;

  private static final String TASK_PREFIX = "task";

  public TaskIdentifier(String id) {
    super(TASK_PREFIX, id);
  }
}
