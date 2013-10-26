package edu.asu.ying.wellington.dfs;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.io.PageHeader;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.ybase.Element;
import edu.asu.ying.wellington.ybase.ElementSerializer;
import edu.asu.ying.wellington.ybase.ElementTooLargeException;
import edu.asu.ying.wellington.ybase.SerializedElement;

/**
 * Serializes elements as they are added and restricts the maximum number of serialized bytes that
 * can be stored in the page.
 */
public final class BoundedSerializedPage<K extends WritableComparable, V extends Writable>
    implements SerializedReadablePage<K, V>, SerializedWritablePage<K, V> {

  private static final Logger log = Logger.getLogger(BoundedSerializedPage.class.getName());

  private final PageMetadata<K, V> metadata;

  private final List<SerializedElement<K, V>> contents = new ArrayList<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  // Used to avoid creating new buffer and writer streams every time we serialize an element.
  private final ElementSerializer serializer = new ElementSerializer();

  private ByteArrayOutputStream serializedBuffer;
  private boolean dirty = true;
  private final HashFunction checksumHasher = Hashing.adler32();


  public BoundedSerializedPage(String tableName, int index, int capacityBytes,
                               Class<K> keyClass, Class<V> valueClass) {

    this.metadata = new PageMetadata<>(PageIdentifier.create(tableName, index),
                                       keyClass, valueClass);

    this.capacityBytes = capacityBytes;
  }

  /**
   * (thread-safe) Adds a serialized element to the page, unless the element would exceed the total
   * or remaining capacity.
   */
  public boolean offer(SerializedElement<K, V> element) throws ElementTooLargeException {

    // Lock on the contents so we don't dirty the page while someone's reading it
    synchronized (contents) {
      if (element.size() > capacityBytes) {
        throw new ElementTooLargeException();
      }
      if (element.size() > getRemainingCapacityBytes()) {
        return false;
      }

      contents.add(element);
      curSizeBytes += element.size();
      metadata.setNumElements(contents.size());
      dirty = true;

      return true;
    }
  }

  @Override
  public void accept(Element<K, V> element) throws IOException {
    if (!offer(serializer.serialize(element))) {
      throw new PageCapacityReachedException();
    }
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

  @Override
  public byte[] toByteArray() {
    // Don't allow the page to be dirtied until we've serialized it
    synchronized (contents) {
      if (dirty) {
        // Write the page to a byte array
        serializedBuffer.reset();
        try {
          PageHeader<K, V> header = new PageHeader<>(metadata);
          header.writeTo(serializedBuffer);

          for (SerializedElement<K, V> element : contents) {
            element.writeTo(serializedBuffer);
          }

        } catch (IOException e) {
          log.log(Level.WARNING, "Uncaught exception serializing page to byte array", e);
          return null;
        }

        // Generate the checksum
        byte[] bytes = serializedBuffer.toByteArray();
        HashCode checksum = checksumHasher.hashBytes(bytes);
        metadata.setChecksum(checksum.asInt());

        return bytes;
      } else {
        return serializedBuffer.toByteArray();
      }
    }
  }

  @Override
  public InputStream getInputStream() {
    return new ByteArrayInputStream(toByteArray());
  }
}
