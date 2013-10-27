package edu.asu.ying.io;

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

  public WritableString() {
  }

  public WritableString(String value) {
    this.value = Preconditions.checkNotNull(value);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.value = in.readUTF();
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeUTF(value);
  }

  @Override
  public int compareTo(WritableString o) {
    return compareTo(o.value);
  }

  public int compareTo(String s) {
    return value.compareTo(s);
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass()) && value
        .equals(((WritableString) o).value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public Iterator<Character> iterator() {
    return Lists.charactersOf(value).iterator();
  }
}
