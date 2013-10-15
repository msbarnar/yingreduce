package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  @Inject
  private RemoteQueueExecutor() {
  }

  @Override
  protected void process(Task task) {
    try {
      Thread.sleep(100 + (new Random()).nextInt(100));
    } catch (InterruptedException e) {
    }
    Serializable result = null;
    try {
      //result = task.run();
      System.out.println(String.format("[%s] Remote: %s", task.getId().toString()));
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
