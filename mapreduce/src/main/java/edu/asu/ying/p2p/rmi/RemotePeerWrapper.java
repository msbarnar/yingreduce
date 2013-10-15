package edu.asu.ying.p2p.rmi;


import com.google.inject.Inject;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.RemotePeer;

/**
 * Provides the implementation of {@code RemotePeer} which will be accessible by remote peers when
 * exported. The proxy implementation glues the remote node interface to the concrete local node
 * implementation while implementing the appropriate patterns to be RMI-compatible.
 */
public final class RemotePeerWrapper implements RemotePeer, Wrapper<RemotePeer> {

  private final RemotePeer proxyInstance;
  private final LocalPeer localPeer;
  private final Activator activator;

  @Inject
  private RemotePeerWrapper(LocalPeer localPeer, Activator activator) {
    this.localPeer = localPeer;
    this.activator = activator;
    this.proxyInstance = activator.bind(RemotePeer.class).toInstance(this);
  }

  @Override
  public PeerIdentifier getIdentifier() throws RemoteException {
    return localPeer.getIdentifier();
  }

  @Override
  public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
    return activator.getReference(cls);
  }

  @Override
  public RemotePeer getProxy() {
    return proxyInstance;
  }
}
