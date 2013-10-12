package edu.asu.ying.wellington.mapreduce.task;

import edu.asu.ying.wellington.mapreduce.net.LocalNode;

/**
 *
 */
public class TaskServer implements TaskService {

  private final LocalNode localNode;

  public TaskServer(LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  public void accept(Task task) throws TaskException {
  }
}
