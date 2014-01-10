package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.io.WritableChar;
import edu.asu.ying.wellington.io.WritableInt;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface RemoteReducer extends Activatable {

  public void collect(Task task, WritableChar key, WritableInt value, boolean isFinished)
      throws RemoteException;

  void commit() throws RemoteException;
}
