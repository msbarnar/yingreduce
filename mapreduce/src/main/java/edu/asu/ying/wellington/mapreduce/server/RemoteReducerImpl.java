package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public final class RemoteReducerImpl implements RemoteReducer {

  private static final long serialVersionUID = 1L;

  private final Reducer reducer;

  public RemoteReducerImpl(Reducer reducer) {
    this.reducer = reducer;
  }

  @Override
  public void collect(Task task, WritableChar key, WritableInt value, boolean isFinished)
      throws RemoteException {
    reducer.collect(task, key, value, isFinished);
  }

  @Override
  public void commit() throws RemoteException {
    reducer.commit();
  }
}
