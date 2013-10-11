package edu.asu.ying.database.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableShort implements WritableComparable<WritableShort> {

  private short value;

  public WritableShort(final short s) {
    this.value = s;
  }

  public void set(final short f) {
    this.value = f;
  }

  public short get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readShort();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeShort(this.value);
  }

  @Override
  public int compareTo(final WritableShort o) {
    final short lhs = this.value;
    final short rhs = o.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  public int compareTo(final short rhs) {
    final short lhs = this.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableShort)) {
      return false;
    }
    WritableShort other = (WritableShort) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return this.value;
  }

  @Override
  public String toString() {
    return Short.toString(this.value);
  }
}
