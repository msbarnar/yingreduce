package edu.asu.ying.wellington.mapreduce.task;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.Serializable;
import java.util.Random;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
@Singleton
public final class RemoteQueueExecutor extends QueueExecutor<Task> {

  private final LocalNode localNode;

  @Inject
  private RemoteQueueExecutor(LocalNode localNode) {
    this.localNode = localNode;
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
      System.out.println(String.format("[%s] Remote: %s", localNode.getID().toString(),
                                       task.getId().toString()));
    } catch (final Exception e) {
      e.printStackTrace();
      result = e;
    }
  }
}
