package edu.asu.ying.dfs.io;

import com.google.common.base.Preconditions;

import java.io.DataOutput;
import java.io.IOException;

import edu.asu.ying.io.Writable;

/**
 *
 */
public class WritableSerializer {

  protected DataOutput out;

  public WritableSerializer(DataOutput out) {
    this.out = Preconditions.checkNotNull(out);
  }

  public void serialize(Writable obj) throws IOException {
    out.writeUTF(obj.getClass().getCanonicalName());
    obj.write(out);
  }
}
