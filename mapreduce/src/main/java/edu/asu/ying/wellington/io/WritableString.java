package edu.asu.ying.wellington.io;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;

/**
 *
 */
public final class WritableString implements WritableComparable<WritableString>,
                                             Iterable<Character> {

  private String value = null;

  public WritableString(final String value) {
    this.value = Preconditions.checkNotNull(value);
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = in.readUTF();
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeUTF(this.value);
  }

  @Override
  public int compareTo(final WritableString o) {
    return this.compareTo(o.value);
  }

  public int compareTo(final String s) {
    return this.value.compareTo(s);
  }

  @Override
  public boolean equals(final Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((WritableString) o).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return this.value;
  }

  @Override
  public Iterator<Character> iterator() {
    return Lists.charactersOf(this.value).iterator();
  }
}