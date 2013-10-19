package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class PageMetadata<K extends WritableComparable, V extends Writable> implements Page<K, V> {

  private final PageIdentifier id;
  private final int numElements;
  private final Class<K> keyClass;
  private final Class<V> valueClass;

  public PageMetadata(PageIdentifier id, int numElements, Class<K> keyClass, Class<V> valueClass) {
    this.id = id;
    this.numElements = numElements;
    this.keyClass = keyClass;
    this.valueClass = value;
  }

  @Override
  public PageIdentifier getID() {
    return id;
  }

  @Override
  public int size() {
    return numElements;
  }

  @Override
  public Class<K> getKeyClass() {
    return keyClass;
  }

  @Override
  public Class<V> getValueClass() {
    return valueClass;
  }
}
