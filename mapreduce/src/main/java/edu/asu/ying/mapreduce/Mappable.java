package edu.asu.ying.mapreduce;

/**
 *
 */
public interface Mappable<K, V, Ko, Vo> {

  void map(K key, V value, OutputCollector<Ko, Vo> output, Reporter reporter);
}
