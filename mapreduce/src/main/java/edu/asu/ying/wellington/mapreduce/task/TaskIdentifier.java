package edu.asu.ying.wellington.mapreduce.task;

import java.util.UUID;

import edu.asu.ying.wellington.AbstractIdentifier;

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

  private static final long serialVersionUID = 1L;

  private TaskIdentifier(String id) {
    super(id);
  }
}
