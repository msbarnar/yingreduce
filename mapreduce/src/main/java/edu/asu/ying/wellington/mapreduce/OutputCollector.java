package edu.asu.ying.wellington.mapreduce;

/**
 *
 */
public interface OutputCollector<K, V> {

  void collect(K key, V value);
}
