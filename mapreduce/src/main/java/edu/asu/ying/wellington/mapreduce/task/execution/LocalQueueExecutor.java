package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import java.util.Random;

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
    try {
      Thread.sleep(200 + (new Random()).nextInt(200));
      System.out.println("Local");
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
