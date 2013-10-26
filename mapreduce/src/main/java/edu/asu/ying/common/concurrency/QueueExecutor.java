package edu.asu.ying.common.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public abstract class QueueExecutor<T> implements Runnable {

  private static final Logger log = Logger.getLogger(QueueExecutor.class.getName());

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

  public void add(T item) {
    queue.add(item);
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
      log.log(Level.WARNING, "Uncaught exception processing queue entry", e);
    }
  }

  protected abstract void process(T item);
}
