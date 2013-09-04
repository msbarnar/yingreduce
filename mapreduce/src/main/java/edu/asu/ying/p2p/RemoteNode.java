package edu.asu.ying.p2p;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;

/**
 * {@code RemoteNode} is a proxy to a {@link LocalNode} in another application domain.
 */
public interface RemoteNode extends Remote, Serializable {

  /**
   * Gets the unique network identifier of the remote node.
   */
  NodeIdentifier getIdentifier() throws RemoteException;

  LocalScheduler getScheduler() throws RemoteException;
}
