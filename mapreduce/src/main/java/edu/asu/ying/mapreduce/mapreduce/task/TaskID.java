package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;
import java.util.UUID;


public final class TaskID implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String id;

  public TaskID() {
    this.id = UUID.randomUUID().toString();
  }

  public TaskID(final TaskID id) {
    this.id = id.toString();
  }

  public TaskID(final UUID uuid) {
    this.id = uuid.toString();
  }

  public TaskID(final String id) {
    this.id = id;
  }

  @Override
  public final String toString() {
    return this.id;
  }

  @Override
  public final boolean equals(final Object o) {
    if (o == this) {
      return true;
    }

    if (o == null) {
      return false;
    }

    if (!(o instanceof TaskID)) {
      return false;
    }

    return this.id.equals(((TaskID) o).toString());
  }

  @Override
  public final int hashCode() {
    return this.id.hashCode();
  }
}
