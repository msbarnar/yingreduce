package edu.asu.ying.mapreduce.common.concurrency.sink;

import edu.asu.ying.mapreduce.common.Sink;

/**
 *
 */
public abstract class AbstractSinkLIFO<E> extends AbstractSinkPump<E> {

  protected AbstractSinkLIFO(Sink<E> sink) {
    super(sink);
  }

  @Override
  protected E next() throws InterruptedException {
    return this.queue.takeLast();
  }
}
