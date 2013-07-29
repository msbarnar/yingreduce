package edu.asu.ying.mapreduce.node.rmi;

import com.google.inject.Injector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public final class ActivatorImpl
    implements Activator {

  private final Injector injector;

  public ActivatorImpl(final Injector injector) {
    this.injector = injector;
  }

  // TODO: implement client-activated, per-call, singleton activation modes
  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T export(final Class<T> type,
                                           final Map<String, String> properties)
      throws RemoteException {

    final Remote instance = this.injector.getInstance(type);
    // TODO: smart port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(5000));
  }

  @Override
  public final <T extends Remote> T export(final T instance,
                                           final Map<String, String> properties)
    throws RemoteException {

    // TODO: smart port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(5000));
  }
}
