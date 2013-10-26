package edu.asu.ying.wellington.ybase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class SerializedElement<K extends WritableComparable, V extends Writable>
    implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final Class<K> keyClass;
  private final Class<V> valueClass;

  private final byte[] key;
  private final byte[] value;
  private final int length;

  @SuppressWarnings("unchecked")
  public SerializedElement(Class<?> keyClass,
                           Class<?> valueClass,
                           byte[] key, byte[] value) {
    this.keyClass = (Class<K>) keyClass;
    this.valueClass = (Class<V>) valueClass;
    this.key = key;
    this.value = value;
    this.length = key.length + value.length;
  }

  public void writeTo(OutputStream stream) throws IOException {
    stream.write(key);
    stream.write(value);
  }

  public byte[] getKey() {
    return this.key;
  }

  public byte[] getValue() {
    return this.value;
  }

  public Class<K> getKeyClass() {
    return keyClass;
  }

  public Class<V> getValueClass() {
    return valueClass;
  }

  public int size() {
    return length;
  }
}
