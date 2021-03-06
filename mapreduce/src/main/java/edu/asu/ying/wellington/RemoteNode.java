package edu.asu.ying.wellington;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.server.RemoteJobService;
import edu.asu.ying.wellington.mapreduce.server.RemoteReducer;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.mapreduce.task.Task;

/**
 *
 */
public interface RemoteNode extends Activatable {

  String getName() throws RemoteException;

  RemoteJobService getJobService() throws RemoteException;

  RemoteTaskService getTaskService() throws RemoteException;

  RemoteDFSService getDFSService() throws RemoteException;

  RemoteReducer getReducerFor(Task task) throws RemoteException;
}
