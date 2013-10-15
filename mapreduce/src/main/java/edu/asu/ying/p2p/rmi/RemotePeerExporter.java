package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

/**
 *
 */
final class RemotePeerExporter implements RemotePeer {

  private Activator activator;

  RemotePeerExporter(Activator activator) {
    this.activator = activator;
  }

  @Override
  public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
    return activator.getReference(cls);
  }
}
