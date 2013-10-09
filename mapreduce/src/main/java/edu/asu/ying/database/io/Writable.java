package edu.asu.ying.database.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;

/**
 *
 */
public interface Writable extends Serializable {

  void readFields(DataInput in) throws IOException;

  void write(DataOutput out) throws IOException;
}
