package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

/**
 *
 */
final class RemotePeerWrapper implements RemotePeer, Wrapper<RemotePeer, Activator> {

  private Activator activator;

  RemotePeerWrapper(Activator activator) {
    this.activator = activator;
  }

  @Override
  public void wrap(Activator target) throws RemoteException {
    this.activator = target;
  }


  @Override
  public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
    return activator.getReference(cls);
  }
}
