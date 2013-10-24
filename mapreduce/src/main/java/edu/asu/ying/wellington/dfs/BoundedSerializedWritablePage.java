package edu.asu.ying.wellington.dfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.wellington.dfs.io.ElementSerializer;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Serializes elements as they are added and restricts the maximum number of serialized bytes that
 * can be stored in the page.
 */
public final class BoundedSerializedWritablePage<K extends WritableComparable, V extends Writable>
    implements WritablePage<K, V>, Iterable<SerializedElement<K, V>> {

  private final PageMetadata<K, V> metadata;

  private final List<SerializedElement<K, V>> contents = new ArrayList<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  // Used to avoid creating new buffer and writer streams every time we serialize an element.
  private final ElementSerializer serializer = new ElementSerializer();


  public BoundedSerializedWritablePage(String tableName, int index, int capacityBytes,
                                       Class<K> keyClass, Class<V> valueClass) {

    this.metadata = new PageMetadata<>(PageIdentifier.create(tableName, index),
                                       keyClass, valueClass);

    this.capacityBytes = capacityBytes;
  }

  /**
   * Adds a serialized element to the page, unless the element would exceed the total or remaining
   * capacity.
   */
  public boolean offer(SerializedElement<K, V> element) throws ElementsExceedPageCapacityException {
    if (element.size() > capacityBytes) {
      throw new ElementsExceedPageCapacityException();
    }
    if (element.size() > getRemainingCapacityBytes()) {
      return false;
    }

    contents.add(element);
    curSizeBytes += element.size();

    return true;
  }

  /**
   * Serializes an element and adds it to the page.
   *
   * @see BoundedSerializedWritablePage#offer(SerializedElement)
   */
  @Override
  public boolean offer(Element<K, V> element) throws ElementsExceedPageCapacityException {
    // Serialize element value
    try {
      return offer(serializer.serialize(element));
    } catch (IOException e) {
      // TODO: Logging
      e.printStackTrace();
      return false;
    }
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
  public PageMetadata<K, V> getMetadata() {
    return metadata;
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

  public Iterator<SerializedElement<K, V>> iterator() {
    return Collections.unmodifiableCollection(contents).iterator();
  }
}
