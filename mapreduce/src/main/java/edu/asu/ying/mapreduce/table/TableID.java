package edu.asu.ying.mapreduce.table;

import java.io.Serializable;

/**
 * A {@code TableID} is a universally unique identifier that uniquely represents a {@link Table} in
 * the network.
 */
public interface TableID extends Serializable {

  @Override
  String toString();
  @Override
  boolean equals(Object o);
  @Override
  int hashCode();
}
