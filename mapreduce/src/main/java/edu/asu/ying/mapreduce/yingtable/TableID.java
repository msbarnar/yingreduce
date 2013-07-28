package edu.asu.ying.mapreduce.yingtable;

import java.io.Serializable;

/**
 * A {@code TableID} is a universally unique identifier that uniquely represents a {@link Table} in
 * the network.
 */
public final class TableID implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final String id;

  public TableID(final String id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return this.id;
  }

  @Override
  public boolean equals(Object o) {
    return o != null && (o == this || o instanceof TableID && this.id.equals(((TableID) o).id));
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
