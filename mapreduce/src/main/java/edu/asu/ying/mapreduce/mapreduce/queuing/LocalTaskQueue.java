package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.task.Task;

/**
 *
 */
public class LocalTaskQueue implements TaskQueue {

  private final BlockingQueue<Task> queue;

  public LocalTaskQueue(final int capacity) {
    this.queue = new LinkedBlockingQueue<>(capacity);
  }

  @Override
  public void start() {
  }

  @Override
  public boolean offer(Task task) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public void run() {
  }
}
