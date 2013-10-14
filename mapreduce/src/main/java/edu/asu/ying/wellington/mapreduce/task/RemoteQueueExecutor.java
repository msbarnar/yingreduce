package edu.asu.ying.wellington.mapreduce.task;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.net.LocalNode;

/**
 *
 */
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  private final LocalNode localNode;

  public RemoteQueueExecutor(LocalNode localNode) {
    this.localNode = localNode;
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
      System.out.println(String.format("[%s] Remote: %s", this.localNode.getId().toString(),
                                       task.getId().toString()));
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
