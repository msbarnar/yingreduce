package edu.asu.ying.mapreduce.database.element;

import java.io.Serializable;

/**
 * Each {@code Element} in a table is indexed by the the arbitrary strings {@code row key} and
 * {@code column key} and a timestamp representing the moment the element was added to the table.
 * The element maps those indices to an arbitrary byte array.
 */
public interface Element extends Serializable {

  public interface Key extends Serializable {
    String getRow();
    String getColumn();
  }

  public interface Value extends Serializable {
    long getSize();
    byte[] getContent();
  }

  Key getKey();
  Value getValue();
}
