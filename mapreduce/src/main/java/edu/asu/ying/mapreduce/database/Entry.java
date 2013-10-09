package edu.asu.ying.mapreduce.database;

import java.io.Serializable;

import edu.asu.ying.mapreduce.io.Writable;
import edu.asu.ying.mapreduce.io.WritableComparable;

/**
 *
 */
public interface Entry<K extends WritableComparable,
    V extends Writable> extends Serializable, Comparable<Entry<K, V>> {

  K getKey();

  V getValue();
}
