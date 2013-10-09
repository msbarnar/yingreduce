package edu.asu.ying.database.io;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableString implements WritableComparable<WritableString> {

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
    return this.value.compareTo(o.value);
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
}
