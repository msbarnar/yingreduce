package edu.asu.ying.mapreduce.task;

import java.util.UUID;

import edu.asu.ying.rmi.AbstractIdentifier;

/**
 *
 */
public final class TaskIdentifier extends AbstractIdentifier {

  public static TaskIdentifier random() {
    return new TaskIdentifier(UUID.randomUUID().toString());
  }

  public static TaskIdentifier forString(String id) {
    return new TaskIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private TaskIdentifier(String id) {
    super(id);
  }
}
