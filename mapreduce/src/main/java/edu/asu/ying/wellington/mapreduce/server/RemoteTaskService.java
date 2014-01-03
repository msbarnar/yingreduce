package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface RemoteTaskService extends Activatable {

  void accept(Task task) throws RemoteException;

  int getBackpressure() throws RemoteException;

  void stop() throws RemoteException;
}
