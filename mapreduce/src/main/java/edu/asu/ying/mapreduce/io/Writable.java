package edu.asu.ying.mapreduce.io;

import java.io.DataInput;
import java.io.DataOutput;

/**
 *
 */
public interface Writable {

  void readFields(DataInput in);

  void write(DataOutput out);
}
