package edu.asu.ying.test;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.OutputCollector;
import edu.asu.ying.wellington.mapreduce.server.RemoteReducer;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public class ExampleCollector implements OutputCollector<WritableChar, WritableInt> {

  private static final Logger log = Logger.getLogger(ExampleCollector.class);

  private final RemoteNode[] reducers;

  private final Task task;

  public ExampleCollector(Task task) {
    this.task = task;

    reducers = new RemoteNode[task.getParentJob().getReducerNodeIDs().size()];
    task.getParentJob().getReducerNodeIDs().toArray(reducers);
  }

  @Override
  public void collect(WritableChar key, WritableInt value) {
    RemoteReducer reducer = pickReducer(key);
    if (reducer != null) {
      try {
        reducer.collect(task, key, value, false);
      } catch (RemoteException e) {
        log.error("Reducer unreachable", e);
      }
    }
  }

  private RemoteReducer pickReducer(WritableChar key) {
    try {
      return reducers[key.hashCode() % reducers.length].getReducerFor(task);
    } catch (RemoteException e) {
      log.error("Reducer unreachable", e);
      return null;
    }
  }

  public void complete() {
    for (RemoteNode node : reducers) {
      try {
        node.getReducerFor(task).collect(task, null, null, true);
      } catch (RemoteException e) {
        log.error("Remote exception signaling completion to reducer", e);
      }
    }
  }
}
