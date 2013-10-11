package edu.asu.ying.p2p.rmi;


import java.rmi.RemoteException;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.RemotePeer;

/**
 * Provides the implementation of {@code RemotePeer} which will be accessible by remote peers when
 * exported. The proxy implementation glues the remote node interface to the concrete local node
 * implementation while implementing the appropriate patterns to be RMI-compatible.
 */
public final class RemotePeerProxy implements RemotePeer {

  private final LocalPeer localPeer;

  public RemotePeerProxy(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public PeerIdentifier getIdentifier() throws RemoteException {
    return this.localPeer.getIdentifier();
  }

  @Override
  public <T extends Activatable> T getReference(final Class<? extends Activatable> cls)
      throws RemoteException {
    return this.localPeer.getActivator().getReference(cls);
  }

  @Override
  public long getCurrentTimeMillis() throws RemoteException {
    return System.currentTimeMillis();
  }
}
