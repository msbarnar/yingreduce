package edu.asu.ying.wellington.rmi;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.dfs.server.RemoteDFSService;
import edu.asu.ying.mapreduce.server.RemoteJobService;
import edu.asu.ying.mapreduce.server.RemoteTaskService;

/**
 *
 */
public interface RemoteNode extends Activatable {

  String getName() throws RemoteException;

  RemoteJobService getJobService() throws RemoteException;

  RemoteTaskService getTaskService() throws RemoteException;

  RemoteDFSService getDFSService() throws RemoteException;
}
