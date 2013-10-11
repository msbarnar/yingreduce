package edu.asu.ying.mapreduce.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;

/**
 *
 */
public interface RemoteNode extends Activatable {

  NodeIdentifier getIdentifier() throws RemoteException;

  RemoteJobService getJobService() throws RemoteException;
}
