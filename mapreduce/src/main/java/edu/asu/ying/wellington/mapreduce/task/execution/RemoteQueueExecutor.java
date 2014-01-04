package edu.asu.ying.wellington.mapreduce.task.execution;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
// FIXME: Abstract execution model away from queues; use unified page retrieval
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  private static final Logger log = Logger.getLogger(RemoteQueueExecutor.class);

  @Inject
  private RemoteQueueExecutor() {
  }

  @Override
  protected void process(Task task) {
    // TODO: Fetch data from peer
    try {
      Thread.sleep(1000 + (new Random()).nextInt(1000));
    } catch (InterruptedException ignored) {
    }
    Serializable result = null;
    try {
      //result = task.run();
      log.info("Remote: " + task.getTargetPageID());
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
