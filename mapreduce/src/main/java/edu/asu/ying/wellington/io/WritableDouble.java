package edu.asu.ying.wellington.io;

import com.google.common.primitives.Doubles;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableDouble implements WritableComparable<WritableDouble> {

  private double value;

  public WritableDouble() {
  }

  public WritableDouble(final double d) {
    this.value = d;
  }

  public void set(final double d) {
    this.value = d;
  }

  public double get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readDouble();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeDouble(this.value);
  }

  @Override
  public int compareTo(final WritableDouble o) {
    final double lhs = this.value;
    final double rhs = o.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  public int compareTo(final double rhs) {
    final double lhs = this.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableDouble)) {
      return false;
    }
    WritableDouble other = (WritableDouble) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return Doubles.hashCode(this.value);
  }

  @Override
  public String toString() {
    return Double.toString(this.value);
  }
}
