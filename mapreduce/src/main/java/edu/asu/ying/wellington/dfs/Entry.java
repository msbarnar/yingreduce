package edu.asu.ying.wellington.dfs;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Entry implements Serializable {

  private final WritableComparable key;
  private final Writable value;

  public Entry(final WritableComparable key, final Writable value) {
    this.key = key;
    this.value = value;
  }

  public final WritableComparable getKey() {
    return this.key;
  }

  public final Writable getValue() {
    return this.value;
  }
}
