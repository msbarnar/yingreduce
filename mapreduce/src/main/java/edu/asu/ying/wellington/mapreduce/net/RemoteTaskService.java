package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface RemoteTaskService extends Activatable {

  void accept(Task task) throws RemoteException;

  int getBackpressure() throws RemoteException;
}
