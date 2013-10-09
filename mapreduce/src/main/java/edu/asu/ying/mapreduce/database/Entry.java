package edu.asu.ying.mapreduce.database;

import java.io.Serializable;

import edu.asu.ying.mapreduce.io.Writable;
import edu.asu.ying.mapreduce.io.WritableComparable;

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
