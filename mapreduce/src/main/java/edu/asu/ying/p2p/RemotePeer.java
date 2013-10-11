package edu.asu.ying.p2p;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * {@code RemotePeer} is a proxy to a {@link LocalPeer} in another application domain.
 */
public interface RemotePeer extends Remote, Serializable {

  /**
   * Gets the unique network identifier of the remote peer.
   */
  PeerIdentifier getIdentifier() throws RemoteException;

  /**
   * Gets a {@link Remote} proxy to an object implementing {@code cls}.
   */
  <T extends Remote> T getProxyToInterface(final Class<?> cls) throws RemoteException;

  /**
   * Gets the current time according to the remote peer.
   */
  long getCurrentTimeMillis() throws RemoteException;
}
