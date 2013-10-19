package edu.asu.ying.wellington.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableChar implements WritableComparable<WritableChar> {

  private char value;

  public WritableChar() {
  }

  public WritableChar(final char c) {
    this.value = c;
  }

  public void set(final char c) {
    this.value = c;
  }

  public char get() {
    return this.value;
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readChar();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeChar(this.value);
  }

  @Override
  public int compareTo(final WritableChar o) {
    return Character.compare(this.value, o.value);
  }

  public int compareTo(final char rhs) {
    return Character.compare(this.value, rhs);
  }

  @Override
  public boolean equals(final Object o) {
    if (!(o instanceof WritableChar)) {
      return false;
    }
    WritableChar other = (WritableChar) o;
    return this.value == other.value;
  }

  @Override
  public int hashCode() {
    return this.value;
  }

  @Override
  public String toString() {
    return Character.toString(this.value);
  }
}
