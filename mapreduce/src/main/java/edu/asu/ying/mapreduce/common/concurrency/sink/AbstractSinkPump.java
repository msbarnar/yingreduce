package edu.asu.ying.mapreduce.common.concurrency.sink;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

import edu.asu.ying.mapreduce.common.Sink;

/**
 *
 */
public abstract class AbstractSinkPump<E> implements Runnable {

  protected final Sink<E> sink;
  protected final BlockingDeque<E> queue = new LinkedBlockingDeque<>();
  protected final ExecutorService threadPool;

  protected AbstractSinkPump(final Sink<E> sink) {
    this.sink = sink;
    this.threadPool = this.createThreadPool();
  }

  public final void start() {
    this.threadPool.submit(this);
  }

  public final void add(final E obj) {
    this.queue.add(obj);
  }

  public boolean offer(final E obj) {
    return this.queue.offer(obj);
  }

  @Override
  public final void run() {
    // Run forever
    this.threadPool.submit(this);

    E obj = null;
    try {
      // Blocks until available
      obj = this.next();
    } catch (final InterruptedException e) {
      // TODO: Logging
      e.printStackTrace();
    }

    if (obj == null) {
      return;
    }

    try {
      this.sink.accept(obj);
    } catch (final IOException e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }

  // Let the derived class decide what kind of thread pool to use
  protected abstract ExecutorService createThreadPool();
  // Let the derived class decide how to remove elements from the queue
  protected abstract E next() throws InterruptedException;
}
