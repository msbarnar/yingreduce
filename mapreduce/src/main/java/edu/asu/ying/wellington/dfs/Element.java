package edu.asu.ying.wellington.dfs;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class Element implements Serializable {

  private static final long SerialVersionUID = 1L;

  protected final WritableComparable key;
  protected final Writable value;

  public Element(WritableComparable key, Writable value) {
    this.key = key;
    this.value = value;
  }

  public WritableComparable getKey() {
    return key;
  }

  public Writable getValue() {
    return value;
  }
}
