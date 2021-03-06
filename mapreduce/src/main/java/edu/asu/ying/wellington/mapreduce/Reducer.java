package edu.asu.ying.wellington.mapreduce;

/**
 *
 */
public interface Reducer<K, V, Ko, Vo> {

  void reduce(K key, Iterable<V> values, OutputCollector<Ko, Vo> output, Reporter reporter);
}
