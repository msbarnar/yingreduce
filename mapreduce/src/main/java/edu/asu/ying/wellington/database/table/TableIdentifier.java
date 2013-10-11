package edu.asu.ying.wellington.database.table;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 */
public final class TableIdentifier implements Serializable, Comparable<TableIdentifier> {

  public static TableIdentifier random() {
    return new TableIdentifier(UUID.randomUUID().toString());
  }

  private static final long SerialVersionUID = 1L;

  private final String id;

  public TableIdentifier(String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull((id)));
  }

  public TableIdentifier forPage(int index) {
    return new TableIdentifier(this.id.concat("~").concat(Integer.toString(index)));
  }

  @Override
  public int compareTo(TableIdentifier o) {
    return this.id.compareTo(o.id);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && this.id
        .equals(((TableIdentifier) o).id);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return this.id;
  }
}
