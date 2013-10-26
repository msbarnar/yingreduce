package edu.asu.ying.wellington.ybase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import edu.asu.ying.wellington.dfs.io.WritableSerializerStream;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Uses a single underlying stream to serialize multiple elements, avoiding the
 * memory and allocation costs of creating a new stream for each writable field.
 */
public final class ElementSerializer {

  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
  WritableSerializerStream ostream = new WritableSerializerStream(buffer);

  public <K extends WritableComparable, V extends Writable> SerializedElement<K, V>
  serialize(Element<K, V> element) throws IOException {

    K key = element.getKey();
    V value = element.getValue();

    byte[] keyBytes, valueBytes;

    buffer.reset();
    ostream.write(key);
    keyBytes = buffer.toByteArray();

    buffer.reset();
    ostream.write(value);
    valueBytes = buffer.toByteArray();

    return new SerializedElement<>(key.getClass(), value.getClass(), keyBytes, valueBytes);
  }
}
