package edu.asu.ying.wellington.io;

import com.google.common.primitives.Floats;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableFloat implements WritableComparable<WritableFloat> {

  private float value;

  public WritableFloat() {
  }

  public WritableFloat(final float f) {
    this.value = f;
  }

  public void set(final float f) {
    this.value = f;
  }

  public float get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readFloat();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeFloat(this.value);
  }

  @Override
  public int compareTo(final WritableFloat o) {
    final float lhs = this.value;
    final float rhs = o.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  public int compareTo(final float rhs) {
    final float lhs = this.value;
    return (lhs > rhs ? 1 : (lhs < rhs ? -1 : 0));
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableFloat)) {
      return false;
    }
    WritableFloat other = (WritableFloat) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return Floats.hashCode(this.value);
  }

  @Override
  public String toString() {
    return Float.toString(this.value);
  }
}
