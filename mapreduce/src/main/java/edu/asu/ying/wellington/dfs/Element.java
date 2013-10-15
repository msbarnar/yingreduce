package edu.asu.ying.wellington.dfs;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class Element<K extends WritableComparable, V extends Writable>
    implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final K key;
  private final V value;

  public Element(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public final K getKey() {
    return key;
  }

  public final V getValue() {
    return value;
  }
}
