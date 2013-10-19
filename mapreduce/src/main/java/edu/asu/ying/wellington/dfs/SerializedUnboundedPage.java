package edu.asu.ying.wellington.dfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code BoundedPage} is limited to a specific capacity in bytes, and will not accept entries that
 * would exceed that capacity.
 */
public final class SerializedUnboundedPage<K extends WritableComparable, V extends Writable>
    implements SerializedPage<K, V>, Sink<Element<K, V>> {

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier pageId;

  private final Class<K> keyClass;
  private final Class<V> valueClass;

  private final List<SerializedElement<K, V>> contents = new ArrayList<>();

  private int curSizeBytes;

  public SerializedUnboundedPage(Page<K, V> metadata) {
    this.pageId = metadata.getID();
    this.keyClass = metadata.getKeyClass();
    this.valueClass = metadata.getValueClass();
  }

  public SerializedUnboundedPage(TableIdentifier parentTableId, int index,
                                 Class<K> keyClass, Class<V> valueClass) {

    this.pageId = PageIdentifier.create(parentTableId, index);

    this.keyClass = keyClass;
    this.valueClass = valueClass;
  }

  public boolean offer(SerializedElement<K, V> element) {
    contents.add(element);
    curSizeBytes += element.length;

    return true;
  }

  @Override
  public boolean offer(Element<K, V> element) {
    return offer(new SerializedElement<>(element));
  }

  /**
   * Adds as many of {@code entries} as possible without exceeding the page's capacity. </p>
   * <b>Fails fast:</b> returns on the first entry that does not fit. Entries must be sorted in
   * descending order of size prior to offering if all possible entries are to be added.
   *
   * @return the number of entries added.
   */
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
  public PageIdentifier getID() {
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

  public int getSizeBytes() {
    return curSizeBytes;
  }

  public boolean isEmpty() {
    return contents.isEmpty();
  }

  @Override
  public Iterator<SerializedElement<K, V>> iterator() {
    return Collections.unmodifiableList(contents).iterator();
  }
}
