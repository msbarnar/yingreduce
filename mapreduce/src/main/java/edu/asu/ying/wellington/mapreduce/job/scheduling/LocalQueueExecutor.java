package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.io.Serializable;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class LocalQueueExecutor extends QueueExecutor<Task> {

  public LocalQueueExecutor() {
  }

  @Override
  protected void process(final Task task) {
    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
