package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;

/**
 *
 */
public interface RemoteNode extends Activatable {

  NodeIdentifier getIdentifier() throws RemoteException;

  RemoteJobService getJobService() throws RemoteException;

  RemoteTaskService getTaskService() throws RemoteException;
}
