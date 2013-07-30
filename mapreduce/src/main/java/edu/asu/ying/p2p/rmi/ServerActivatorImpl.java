package edu.asu.ying.p2p.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;


/**
 * Controls creation and lifetime management for server-side object instances available for
 * accession by remote nodes.
 */
public final class ServerActivatorImpl implements Activator, ServerActivator {



  public ServerActivatorImpl() {
  }

  /*
   * ServerActivator
   */
  @Override
  public <T extends Remote> ActivatorBinding bind(Class<T> type) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T export(final T instance,
                                           final Map<String, String> properties)
    throws RemoteException {

    // TODO: smart port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(5000));
  }

  @Override
  public <T extends Remote> T getReference(Class<T> type,
                                           @Nullable Map<String, String> properties) {
    return null;
  }
}
