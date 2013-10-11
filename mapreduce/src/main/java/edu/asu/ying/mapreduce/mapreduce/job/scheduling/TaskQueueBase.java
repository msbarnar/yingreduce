package edu.asu.ying.mapreduce.mapreduce.job.scheduling;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public abstract class TaskQueueBase implements TaskQueue {

  protected final LocalScheduler scheduler;

  private final BlockingQueue<Task> queue = new LinkedBlockingQueue<>();

  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  protected TaskQueueBase(final LocalScheduler scheduler) {
    this.scheduler = scheduler;
  }


  public final void start() {
    this.threadPool.submit(this);
  }


  public boolean offer(final Task task) {
    return this.queue.offer(task);
  }


  public final int size() {
    return this.queue.size();
  }


  public final void run() {
    // Run forever
    this.threadPool.submit(this);

    Task task = null;
    try {
      // Blocks until available
      task = this.queue.take();
    } catch (final InterruptedException e) {
      // TODO: Logging
      e.printStackTrace();
    }

    if (task == null) {
      return;
    }

    this.runTask(task);
  }

  protected abstract void runTask(final Task task);
}
