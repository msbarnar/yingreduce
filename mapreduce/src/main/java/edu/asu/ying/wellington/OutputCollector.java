package edu.asu.ying.wellington;

/**
 *
 */
public interface OutputCollector<K, V> {

  void collect(K key, V value);
}
