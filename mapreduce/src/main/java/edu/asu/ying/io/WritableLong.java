package edu.asu.ying.io;

import com.google.common.primitives.Longs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableLong implements WritableComparable<WritableLong> {

  private long value;

  public WritableLong() {
  }

  public WritableLong(final long l) {
    this.value = l;
  }

  public void set(final long l) {
    this.value = l;
  }

  public long get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readLong();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeLong(this.value);
  }

  @Override
  public int compareTo(final WritableLong o) {
    final long lhs = this.value;
    final long rhs = o.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  public int compareTo(final long rhs) {
    final long lhs = this.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableLong)) {
      return false;
    }
    WritableLong other = (WritableLong) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return Longs.hashCode(this.value);
  }

  @Override
  public String toString() {
    return Long.toString(this.value);
  }
}
