package edu.asu.ying.wellington.dfs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.wellington.dfs.io.WritableDeserializerStream;
import edu.asu.ying.wellington.dfs.io.WritableSerializerStream;
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
  public final int length;

  public SerializedElement(Element<K, V> element) {
    this(element.getKey(), element.getValue());
  }

  public SerializedElement(Class<K> keyClass,
                           Class<V> valueClass,
                           byte[] key, byte[] value) {
    this.keyClass = keyClass;
    this.valueClass = valueClass;
    this.key = key;
    this.value = value;
    this.length = key.length + value.length;
  }

  @SuppressWarnings("unchecked")
  public SerializedElement(K key, V value) {

    this.keyClass = (Class<K>) key.getClass();
    this.valueClass = (Class<V>) value.getClass();

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    WritableSerializerStream writer = new WritableSerializerStream(buffer);

    try {
      writer.write(key);
      this.key = buffer.toByteArray();

      buffer.reset();
      writer.write(value);
      this.value = buffer.toByteArray();

      buffer.close();
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    this.length = this.key.length + this.value.length;
  }

  public Element<K, V> deserialize() throws IOException {
    K key;
    V value;
    WritableDeserializerStream stream
        = new WritableDeserializerStream(new ByteArrayInputStream(this.key));

    key = stream.read(keyClass);
    stream.open(new ByteArrayInputStream(this.value));
    value = stream.read(valueClass);

    return new Element<>(key, value);
  }

  public byte[] getKey() {
    return this.key;
  }

  public byte[] getValue() {
    return this.value;
  }
}
