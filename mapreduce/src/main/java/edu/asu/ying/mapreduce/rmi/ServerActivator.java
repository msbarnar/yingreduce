package edu.asu.ying.mapreduce.rmi;

import com.google.inject.Inject;
import com.google.inject.Injector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;

import edu.asu.ying.mapreduce.rmi.Activator;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public final class ServerActivator
    implements Activator {

  private final Injector injector;

  public ServerActivator(final Injector injector) {
    this.injector = injector;
  }

  // TODO: implement client-activated, per-call, singleton activation modes
  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T getReference(final Class<T> type,
                                                 final Map<String, String> properties)
      throws RemoteException {

    final Remote instance = this.injector.getInstance(type);
    // TODO: smart port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(2000));
  }
}
