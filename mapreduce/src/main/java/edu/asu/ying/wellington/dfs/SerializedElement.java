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

  private final WritableComparable key;
  private final byte[] value;

  public SerializedElement(final Element element) {
    this(element.getKey(), element.getValue());
  }

  public SerializedElement(final WritableComparable key, final byte[] value) {
    this.key = key;
    this.value = value;
  }

  public <V extends Writable> SerializedElement(final WritableComparable key,
                                                final Writable value) {
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try {
      value.write(new DataOutputStream(buffer));
    } catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    this.key = key;
    this.value = buffer.toByteArray();
  }

  public final WritableComparable getKey() {
    return this.key;
  }

  public final byte[] getValue() {
    return this.value;
  }
}
