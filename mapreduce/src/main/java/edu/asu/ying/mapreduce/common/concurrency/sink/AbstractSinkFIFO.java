package edu.asu.ying.mapreduce.common.concurrency.sink;

import edu.asu.ying.mapreduce.common.Sink;

/**
 *
 */
public abstract class AbstractSinkFIFO<E> extends AbstractSinkPump<E> {

  protected AbstractSinkFIFO(Sink<E> sink) {
    super(sink);
  }

  @Override
  protected E next() throws InterruptedException {
    return this.queue.takeFirst();
  }
}
