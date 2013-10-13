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
public final class RemotePeerWrapperFactory implements WrapperFactory<LocalPeer, RemotePeer> {

  @Override
  public RemotePeer create(LocalPeer target, Activator activator) {
    return new RemotePeerWrapper(target);
  }

  private final class RemotePeerWrapper implements RemotePeer {

    private final LocalPeer localPeer;

    private RemotePeerWrapper(LocalPeer localPeer) {
      this.localPeer = localPeer;
    }

    @Override
    public PeerIdentifier getIdentifier() throws RemoteException {
      return this.localPeer.getIdentifier();
    }

    @Override
    public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
      return this.localPeer.getActivator().getReference(cls);
    }
  }
}
