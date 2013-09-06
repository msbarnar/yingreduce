package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;
import edu.asu.ying.mapreduce.mapreduce.task.TaskCompletion;

/**
 *
 */
public final class RemoteTaskQueue extends TaskQueueBase {

  protected RemoteTaskQueue(final int capacity, final LocalScheduler scheduler) {
    super(capacity, scheduler);
  }

  @Override
  public boolean offer(final Task task) {
    if (super.offer(task)) {
      System.out.println("[Remote] Task added");
      return true;
    } else {
      return false;
    }
  }

  @Override
  protected void runTask(final Task task) {
    // TODO: logging
    System.out.println("[Remote] Fetching remote content for task ".concat(
        task.getId().toString()));

    try {
      Thread.sleep(100+(new Random()).nextInt(300));
    } catch (final InterruptedException e) {}

    System.out.println("[Remote] Starting task ".concat(task.getId().toString()));

    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }

    System.out.println("[Remote] Task complete; sending to reducer: ".concat(
        task.getId().toString()));

    this.scheduler.completeTask(new TaskCompletion(task, result));
  }
}
