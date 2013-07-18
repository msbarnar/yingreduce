package edu.asu.ying.mapreduce.task;

import java.util.UUID;

/**
 * Uniquely idenfies a {@link Job}.
 */
public final class JobID {

  private final UUID id;

  public JobID() {
    this.id = UUID.randomUUID();
  }
  public JobID(final JobID id) {
    this.id = id.toUUID();
  }
  public JobID(final UUID uuid) {
    this.id = uuid;
  }
  public JobID(final String id) {
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

    if (!(o instanceof JobID))
      return false;

    return this.id.equals(((JobID) o).toUUID());
  }

  @Override
  public final int hashCode() {
    return this.id.hashCode();
  }
}
