package edu.asu.ying.wellington.dfs;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Element implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final WritableComparable key;
  private final Writable value;

  public Element(WritableComparable key, Writable value) {
    this.key = key;
    this.value = value;
  }

  public final WritableComparable getKey() {
    return key;
  }

  public final Writable getValue() {
    return value;
  }
}
