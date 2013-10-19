package edu.asu.ying.wellington.dfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Serializes elements as they are added and restricts the maximum number of serialized bytes that
 * can be stored in the page.
 */
public final class SerializingBoundedPage<K extends WritableComparable, V extends Writable>
    implements SerializingPage<K, V>, Sink<Element<K, V>> {

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier pageId;

  private final Class<K> keyClass;
  private final Class<V> valueClass;

  private final List<SerializedElement<K, V>> contents = new ArrayList<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  public SerializingBoundedPage(TableIdentifier parentTableId, int index, int capacityBytes,
                                Class<K> keyClass, Class<V> valueClass) {

    this.pageId = PageIdentifier.create(parentTableId, index);

    this.keyClass = keyClass;
    this.valueClass = valueClass;

    this.capacityBytes = capacityBytes;
  }

  public boolean offer(SerializedElement<K, V> element) throws ElementsExceedPageCapacityException {
    if (element.length > capacityBytes) {
      throw new ElementsExceedPageCapacityException();
    }
    if (element.length > getRemainingCapacityBytes()) {
      return false;
    }

    contents.add(element);
    curSizeBytes += element.length;

    return true;
  }

  @Override
  public boolean offer(Element<K, V> element) throws ElementsExceedPageCapacityException {
    // Serialize element value
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
  public int offer(Iterable<Element<K, V>> elements) throws ElementsExceedPageCapacityException {
    List<WritableComparable> oversizedElements = null;
    int numAdded = 0;
    for (Element<K, V> element : elements) {
      try {
        if (offer(element)) {
          numAdded++;
        }
      } catch (ElementsExceedPageCapacityException e) {
        if (oversizedElements == null) {
          oversizedElements = new ArrayList<>();
        }
        oversizedElements.add(element.getKey());
      }
    }
    if (oversizedElements != null) {
      throw new ElementsExceedPageCapacityException(oversizedElements);
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

  public int getCapacityBytes() {
    return capacityBytes;
  }

  public int getRemainingCapacityBytes() {
    return capacityBytes - curSizeBytes;
  }

  public int sizeBytes() {
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
