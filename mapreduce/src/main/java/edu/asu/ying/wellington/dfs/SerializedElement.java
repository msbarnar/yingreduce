package edu.asu.ying.wellington.dfs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class SerializedElement implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final byte[] key;
  private final byte[] value;
  public final int length;

  public SerializedElement(Element element) {
    this(element.getKey(), element.getValue());
  }

  public SerializedElement(byte[] key, byte[] value) {
    this.key = key;
    this.value = value;
    this.length = key.length + value.length;
  }

  public <V extends Writable> SerializedElement(WritableComparable key, Writable value) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream writer = new DataOutputStream(buffer);

    try {
      key.write(writer);
      this.key = buffer.toByteArray();

      buffer.reset();
      value.write(writer);
      this.value = buffer.toByteArray();

      buffer.close();
    } catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    this.length = this.key.length + this.value.length;
  }

  public byte[] getKey() {
    return this.key;
  }

  public byte[] getValue() {
    return this.value;
  }
}
