package edu.asu.ying.p2p;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activatable;

/**
 * {@code RemotePeer} is a proxy to a {@link LocalPeer} in another application domain.
 */
public interface RemotePeer extends Activatable {

  /**
   * Gets the unique network identifier of the remote peer.
   */
  PeerIdentifier getIdentifier() throws RemoteException;

  /**
   * Gets a {@link Remote} proxy to an object implementing {@code cls}.
   */
  <T extends Activatable> T getReference(Class<? extends Activatable> cls) throws RemoteException;

  /**
   * Gets the current time according to the remote peer.
   */
  long getCurrentTimeMillis() throws RemoteException;
}
