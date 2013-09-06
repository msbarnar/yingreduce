package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;

/**
 *
 */
public abstract class TaskQueueBase implements TaskQueue {

  protected final LocalScheduler scheduler;

  private final BlockingQueue<Task> queue;

  // One task at a time
  private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

  protected TaskQueueBase(final int capacity, final LocalScheduler scheduler) {
    this.queue = new LinkedBlockingQueue<>(capacity);
    this.scheduler = scheduler;
  }

  @Override
  public final void start() {
    this.threadPool.submit(this);
  }

  @Override
  public boolean offer(final Task task) {
    return this.queue.offer(task);
  }

  @Override
  public final int size() {
    return this.queue.size();
  }

  @Override
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
