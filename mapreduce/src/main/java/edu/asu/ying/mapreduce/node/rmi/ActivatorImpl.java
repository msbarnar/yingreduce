package edu.asu.ying.mapreduce.node.rmi;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;

import edu.asu.ying.mapreduce.node.LocalNode;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public final class ActivatorImpl
    implements Activator {

  public ActivatorImpl() {
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T export(final T instance,
                                           final Map<String, String> properties)
    throws RemoteException {

    // TODO: smart port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(5000));
  }
}
