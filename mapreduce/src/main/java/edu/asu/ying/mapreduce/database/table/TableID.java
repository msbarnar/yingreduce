package edu.asu.ying.mapreduce.database.table;

import java.io.Serializable;
import java.util.UUID;

/**
 * {@code TableID} uniquely identifies a table in the database.
 */
public final class TableID implements Serializable {

  public static TableID createRandom() {
    return new TableID(UUID.randomUUID().toString());
  }

  public static TableID fromString(final String id) {
    return new TableID(id);
  }

  /**
   * Generates a unique TableID naming a table of results from the processing of a sender table.
   * </p> The generated table ID begins with the sender table ID followed by a unique string.
   *
   * @param sourceTable the table from which the results are generated.
   */
  public static TableID createResultTable(final TableID sourceTable) {
    return fromString(sourceTable.toString().concat("|").concat(UUID.randomUUID().toString()));
  }

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
