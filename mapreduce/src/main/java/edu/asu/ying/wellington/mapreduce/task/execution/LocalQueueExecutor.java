package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import java.io.Serializable;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class LocalQueueExecutor extends QueueExecutor<Task> {

  @Inject
  private LocalQueueExecutor() {
  }

  @Override
  protected void process(Task task) {
    Serializable result = null;
    try {
      //result = task.run();
      Thread.sleep(10);
      System.out.println(String.format("Local: %s", task.getId().toString()));
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
