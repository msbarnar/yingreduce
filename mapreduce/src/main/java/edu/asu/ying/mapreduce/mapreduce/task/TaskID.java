package edu.asu.ying.mapreduce.mapreduce.task;

import java.io.Serializable;
import java.util.UUID;


public final class TaskID implements Serializable {

  private static final long serialVersionUID = 1L;

  private final UUID id;

  public TaskID() {
    this.id = UUID.randomUUID();
  }
  public TaskID(final TaskID id) {
    this.id = id.toUUID();
  }
  public TaskID(final UUID uuid) {
    this.id = uuid;
  }
  public TaskID(final String id) {
    this.id = UUID.fromString(id);
  }

  public final UUID toUUID() {
    return this.id;
  }

  @Override
  public final String toString() {
    return this.id.toString();
  }
  
  @Override
  public final boolean equals(final Object o) {
    if (o == this)
      return true;

    if (o == null)
      return false;

    if (!(o instanceof TaskID))
      return false;

    return this.id.equals(((TaskID) o).toUUID());
  }

  @Override
  public final int hashCode() {
    return this.id.hashCode();
  }
}
