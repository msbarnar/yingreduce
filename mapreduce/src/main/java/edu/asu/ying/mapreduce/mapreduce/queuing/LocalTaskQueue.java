package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskCompletion;

/**
 *
 */
public final class LocalTaskQueue extends TaskQueueBase {

  public LocalTaskQueue(final int capacity, final LocalScheduler scheduler) {
    super(capacity, scheduler);
  }

  @Override
  public boolean offer(final Task task) {
    if (super.offer(task)) {
      System.out.println("[Local] Task added");
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected void runTask(final Task task) {
    System.out.println("[Local] Starting task ".concat(task.getId().toString()));

    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }

    System.out.println("[Local] Sending to reducer: ".concat(
        task.getId().toString()));

    this.scheduler.completeTask(new TaskCompletion(task, result));
  }
}
