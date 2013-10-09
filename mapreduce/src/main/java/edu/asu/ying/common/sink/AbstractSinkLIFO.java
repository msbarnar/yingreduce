package edu.asu.ying.common.sink;

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
