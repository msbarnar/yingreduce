package edu.asu.ying.wellington.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.wellington.rmi.VersionMismatchException;

/**
 *
 */
public abstract class WritableVersioned implements Writable {

  protected abstract byte getVersion();

  @Override
  public void readFields(DataInput in) throws IOException {
    byte version = in.readByte();
    if (version != getVersion()) {
      throw new VersionMismatchException(getVersion(), version);
    }
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.write(getVersion());
  }
}
