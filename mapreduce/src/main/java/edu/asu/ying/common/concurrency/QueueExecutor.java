package edu.asu.ying.common.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public abstract class QueueExecutor<T> implements Runnable {

  private final BlockingQueue<T> queue;
  private final ExecutorService threadPool;

  /**
   * Starts a single-threaded executor
   */
  protected QueueExecutor() {
    this(Executors.newSingleThreadExecutor());
  }

  protected QueueExecutor(ExecutorService executor) {
    this(executor, new LinkedBlockingQueue<T>());
  }

  protected QueueExecutor(ExecutorService executor, BlockingQueue<T> queue) {
    this.threadPool = executor;
    this.queue = queue;
  }

  public void start() {
    threadPool.submit(this);
  }

  public void put(T item) throws InterruptedException {
    queue.put(item);
  }

  public int size() {
    return queue.size();
  }

  @Override
  public void run() {
    // Run forever
    threadPool.submit(this);

    T item = null;
    try {
      // Blocks until available
      item = queue.take();
    } catch (InterruptedException ignored) {
    }

    if (item == null) {
      return;
    }

    try {
      this.process(item);
    } catch (Throwable e) {
      // TODO: Logging
      e.printStackTrace();
    }
  }

  protected abstract void process(T item);
}
