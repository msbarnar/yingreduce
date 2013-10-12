package edu.asu.ying.wellington.mapreduce.job.scheduling;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  public RemoteQueueExecutor() {
  }

  @Override
  protected void process(final Task task) {
    try {
      Thread.sleep(100 + (new Random()).nextInt(100));
    } catch (final InterruptedException e) {
    }
    Serializable result = null;
    try {
      result = task.run();
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
