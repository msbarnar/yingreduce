package edu.asu.ying.mapreduce.database;

import com.google.common.primitives.Longs;

import java.util.Comparator;
import java.util.Map;

/**
 */
public class ValueSizeComparator implements Comparator<Map.Entry<Key, Value>> {

  @Override
  public int compare(final Map.Entry<Key, Value> a,
                     final Map.Entry<Key, Value> b) {
    return Longs.compare(a.getValue().getSize(), b.getValue().getSize());
  }
}
