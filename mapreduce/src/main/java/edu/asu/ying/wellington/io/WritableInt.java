package edu.asu.ying.wellington.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableInt implements WritableComparable<WritableInt> {

  public static final int SIZE = 4;

  private int value;

  public WritableInt() {
  }

  public WritableInt(final int i) {
    this.value = i;
  }

  public void set(final int f) {
    this.value = f;
  }

  public int get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readInt();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeInt(this.value);
  }

  @Override
  public int compareTo(final WritableInt o) {
    final int lhs = this.value;
    final int rhs = o.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  public int compareTo(final int rhs) {
    final int lhs = this.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableInt)) {
      return false;
    }
    WritableInt other = (WritableInt) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return this.value;
  }

  @Override
  public String toString() {
    return Integer.toString(this.value);
  }
}
