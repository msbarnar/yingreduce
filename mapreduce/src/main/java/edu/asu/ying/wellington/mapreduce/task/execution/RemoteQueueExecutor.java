package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
// FIXME: Abstract execution model away from queues; use unified page retrieval
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  @Inject
  private RemoteQueueExecutor() {
  }

  @Override
  protected void process(Task task) {
    // TODO: Fetch data from peer
    try {
      Thread.sleep(500 + (new Random()).nextInt(500));
    } catch (InterruptedException ignored) {
    }
    Serializable result = null;
    try {
      //result = task.run();
      System.out.println("Remote");
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
