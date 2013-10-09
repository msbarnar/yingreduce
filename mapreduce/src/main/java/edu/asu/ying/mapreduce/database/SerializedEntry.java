package edu.asu.ying.mapreduce.database;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.mapreduce.io.Writable;
import edu.asu.ying.mapreduce.io.WritableComparable;

/**
 *
 */
public final class SerializedEntry implements Serializable {

  private final WritableComparable key;
  private final byte[] value;

  public SerializedEntry(final Entry entry) {
    this(entry.getKey(), entry.getValue());
  }

  public SerializedEntry(final WritableComparable key, final byte[] value) {
    this.key = key;
    this.value = value;
  }

  public <V extends Writable> SerializedEntry(final WritableComparable key, final Writable value) {
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
