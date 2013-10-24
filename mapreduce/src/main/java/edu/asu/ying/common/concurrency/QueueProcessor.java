package edu.asu.ying.common.concurrency;

/**
 *
 */
public interface QueueProcessor<T> {

  void process(T item);
}
