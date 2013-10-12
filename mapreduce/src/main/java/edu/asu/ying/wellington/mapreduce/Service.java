package edu.asu.ying.wellington.mapreduce;

/**
 *
 */
public interface Service<T> {

  Class<? extends T> getWrapper();
}
