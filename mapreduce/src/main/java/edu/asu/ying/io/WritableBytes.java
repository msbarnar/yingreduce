package edu.asu.ying.io;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * {@code WriteableBytes} provides a serializable primitive for a byte array.
 */
public final class WritableBytes implements WritableComparable<WritableBytes> {

  // The number of bytes prepended to the byte array
  public static final int SIZE = 4;

  private byte[] value = null;

  public WritableBytes() {
  }

  public WritableBytes(byte[] value) {
    this.value = Preconditions.checkNotNull(value);
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    this.value = new byte[in.readInt()];
    in.readFully(value, 0, value.length);
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(this.value.length);
    out.write(this.value);
  }

  public byte[] toByteArray() {
    return this.value;
  }

  /**
   * Gets the number of bytes of the serialized byte array including prepended array length.
   */
  public int size() {
    return SIZE + value.length;
  }

  @Override
  public int compareTo(WritableBytes o) {
    return this.compareTo(o.value);
  }

  public int compareTo(byte[] b) {
    return ByteBuffer.wrap(this.value).compareTo(ByteBuffer.wrap(b));
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
