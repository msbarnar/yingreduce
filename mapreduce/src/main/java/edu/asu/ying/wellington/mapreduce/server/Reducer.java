package edu.asu.ying.wellington.mapreduce.server;

import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface Reducer {

  public void collect(Task task, WritableChar key, WritableInt value, boolean isFinished);

  void commit();
}
