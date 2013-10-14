package edu.asu.ying.wellington.mapreduce.task;

import java.io.Serializable;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
public final class LocalQueueExecutor extends QueueExecutor<Task> {

  private final LocalNode localNode;

  public LocalQueueExecutor(LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  protected void process(final Task task) {
    Serializable result = null;
    try {
      //result = task.run();
      System.out.println(String.format("[%s] Local: %s", this.localNode.getId().toString(),
                                       task.getId().toString()));
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
