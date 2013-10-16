package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

/**
 *
 */
public interface RemoteNode extends Activatable {

  String getName() throws RemoteException;

  RemoteJobService getJobService() throws RemoteException;

  RemoteTaskService getTaskService() throws RemoteException;

  RemoteDFSService getDFSService() throws RemoteException;
}
