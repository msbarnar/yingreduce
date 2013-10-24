package edu.asu.ying.wellington.dfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class UnboundedPage<K extends WritableComparable, V extends Writable>
    implements Page<K, V> {

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier pageId;

  private final Class<K> keyClass;
  private final Class<V> valueClass;

  private final List<Element<K, V>> contents = new ArrayList<>();

  public UnboundedPage(HasPageMetadata<K, V> metadata) {
    this.pageId = metadata.getId();
    this.keyClass = metadata.getKeyClass();
    this.valueClass = metadata.getValueClass();
  }

  public UnboundedPage(PageIdentifier id, Class<K> keyClass, Class<V> valueClass) {
    this.pageId = id;

    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  public UnboundedPage(String tableName, int index,
                       Class<K> keyClass, Class<V> valueClass) {

    this(PageIdentifier.create(tableName, index), keyClass, valueClass);
  }

  public boolean offer(SerializedElement<K, V> element) {
    try {
      return offer(element.deserialize());
    } catch (IOException e) {
      // TODO: Logging
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean offer(Element<K, V> element) {
    contents.add(element);

    return true;
  }

  @Override
  public int offer(Iterable<Element<K, V>> elements) {
    int numAdded = 0;
    for (Element<K, V> element : elements) {
      if (offer(element)) {
        numAdded++;
      }
    }
    return numAdded;
  }

  @Override
  public PageIdentifier getId() {
    return pageId;
  }

  @Override
  public Class<K> getKeyClass() {
    return keyClass;
  }

  @Override
  public Class<V> getValueClass() {
    return valueClass;
  }

  @Override
  public int size() {
    return contents.size();
  }

  public boolean isEmpty() {
    return contents.isEmpty();
  }

  @Override
  public Iterator<Element<K, V>> iterator() {
    return Collections.unmodifiableList(contents).iterator();
  }
}
