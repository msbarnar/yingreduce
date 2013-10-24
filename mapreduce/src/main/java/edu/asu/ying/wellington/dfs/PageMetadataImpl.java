package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class PageMetadataImpl<K extends WritableComparable, V extends Writable>
    implements HasPageMetadata<K, V> {

  private final PageIdentifier id;
  private final int numElements;
  private final Class<K> keyClass;
  private final Class<V> valueClass;

  public PageMetadataImpl(PageIdentifier id,
                          int numElements,
                          Class<K> keyClass,
                          Class<V> valueClass) {
    this.id = id;
    this.numElements = numElements;
    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  @Override
  public PageIdentifier getId() {
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
