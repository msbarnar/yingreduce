package edu.asu.ying.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.RemoteActivator;

/**
 * {@code RemoteNode} is a proxy to a {@link LocalNode} in another application domain.
 */
public interface RemoteNode extends Remote {

  /**
   * Gets the unique network identifier of the remote node.
   */
  NodeIdentifier getIdentifier() throws RemoteException;

  /**
   * Gets an {@link edu.asu.ying.p2p.rmi.RemoteActivator} capable of providing instances of desired classes.
   */
  RemoteActivator getActivator() throws RemoteException;
}
