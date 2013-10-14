package edu.asu.ying.wellington.mapreduce.task;

import java.util.UUID;

import edu.asu.ying.wellington.Identifier;

/**
 *
 */
public final class TaskIdentifier extends Identifier {

  public static TaskIdentifier random() {
    return new TaskIdentifier(UUID.randomUUID().toString());
  }

  public static TaskIdentifier forString(String id) {
    return new TaskIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private static final String TASK_PREFIX = "task";

  private TaskIdentifier(String id) {
    super(TASK_PREFIX, id);
  }
}
