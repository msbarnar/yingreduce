package edu.asu.ying.mapreduce.mapreduce.queuing;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.task.Task;

/**
 *
 */
public final class RemoteTaskQueue extends TaskQueueBase {

  public RemoteTaskQueue(final LocalScheduler scheduler) {
    super(scheduler);
  }

  @Override
  protected void runTask(final Task task) {
    // TODO: logging
    /*System.out.println("[Remote] Fetching remote content for task ".concat(
        task.getId().toString()));*/

    try {
      Thread.sleep(100 + (new Random()).nextInt(100));
    } catch (final InterruptedException e) {
    }

    //System.out.println("[Remote] ".concat(task.getId().toString()));

    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }

    //System.out.println("[Remote] Complete ".concat(task.getId().toString()));

    this.scheduler.completeTask(new TaskCompletion(task, result));
  }
}
