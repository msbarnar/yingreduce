package edu.asu.ying.p2p;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activatable;
import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;

/**
 *
 */
public final class RemotePeerExporter implements RemotePeer, Exporter<Activator, RemotePeer> {

  private Activator activator;
  private RemotePeer proxyInstance;

  @Inject
  private RemotePeerExporter(Activator activator) {
    this.activator = activator;
  }

  @Override
  public RemotePeer export(Activator target) throws ExportException {
    proxyInstance = activator.bind(RemotePeer.class, this);
    return proxyInstance;
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
