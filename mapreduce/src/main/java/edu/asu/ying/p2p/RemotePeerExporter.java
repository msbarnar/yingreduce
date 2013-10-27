package edu.asu.ying.p2p;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.rmi.Activatable;
import edu.asu.ying.rmi.Activator;
import edu.asu.ying.rmi.Exporter;

/**
 *
 */
public final class RemotePeerExporter implements RemotePeer, Exporter<LocalPeer, RemotePeer> {

  private LocalPeer localPeer;
  private Activator activator;
  private RemotePeer proxyInstance;

  @Inject
  private RemotePeerExporter(LocalPeer localPeer,
                             Activator activator) {
    this.localPeer = localPeer;
    this.activator = activator;
  }

  @Override
  public RemotePeer export(LocalPeer target) throws ExportException {
    proxyInstance = activator.bind(RemotePeer.class, this);
    return proxyInstance;
  }

  @Override
  public String getName() throws RemoteException {
    return localPeer.getName();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
    if (cls.equals(RemotePeer.class)) {
      return (T) proxyInstance;
    } else {
      return activator.getReference(cls);
    }
  }
}
