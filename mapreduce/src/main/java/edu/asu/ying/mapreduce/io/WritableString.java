package edu.asu.ying.mapreduce.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 */
public final class WritableString implements Writable, Comparable<WritableString> {

  private String value = null;

  public WritableString(final String value) {
    this.value = value;
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
    return 0;
  }
}
