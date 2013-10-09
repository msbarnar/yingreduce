package edu.asu.ying.database.io;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public final class WritableBytes implements WritableComparable<WritableBytes> {

  private byte[] value = null;

  public WritableBytes(final byte[] value) {
    this.value = Preconditions.checkNotNull(value);
  }

  @Override
  public void readFields(final DataInput in) throws IOException {
    this.value = new byte[in.readInt()];
    in.readFully(this.value);
  }

  @Override
  public void write(final DataOutput out) throws IOException {
    out.writeInt(this.value.length);
    out.write(this.value);
  }

  public byte[] toByteArray() {
    return this.value;
  }

  @Override
  public int compareTo(final WritableBytes o) {
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass())
                        && Arrays.equals(this.value, ((WritableBytes) o).toByteArray());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(this.value);
  }
}
