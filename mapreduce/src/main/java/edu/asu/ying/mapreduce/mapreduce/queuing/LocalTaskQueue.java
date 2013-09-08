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

  public LocalTaskQueue(final LocalScheduler scheduler) {
    super(scheduler);
  }

  @Override
  protected void runTask(final Task task) {
    // TODO: Logging
    //System.out.println("[Local] Start ".concat(task.getId().toString()));

    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }

    //System.out.println("[Local] Complete ".concat(task.getId().toString()));

    this.scheduler.completeTask(new TaskCompletion(task, result));
  }
}
