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
    return activator.bind(RemotePeer.class, this);
  }

  @Override
  public <T extends Activatable> T getReference(Class<T> cls) throws RemoteException {
    return activator.getReference(cls);
  }
}
