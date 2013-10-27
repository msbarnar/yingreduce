package edu.asu.ying.mapreduce;

/**
 *
 */
public interface OutputCollector<K, V> {

  void collect(K key, V value);
}
