package edu.asu.ying.wellington.dfs;

import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class Element<K extends WritableComparable, V extends Writable> implements Serializable {

  private static final long SerialVersionUID = 1L;

  protected final K key;
  protected final V value;

  public Element(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }
}
