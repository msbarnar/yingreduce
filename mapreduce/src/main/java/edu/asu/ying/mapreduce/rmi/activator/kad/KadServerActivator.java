package edu.asu.ying.mapreduce.rmi.activator.kad;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Random;

import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public final class KadServerActivator
    implements Activator {

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T getReference(final Class<T> type,
                                                 final Map<String, String> properties)
      throws RemoteException {

    final Injector injector = Guice.createInjector(new KademliaNetwork());
    final Remote instance = injector.getInstance(type);
    // TODO: port provision
    return (T) UnicastRemoteObject.exportObject(instance, 8000 + (new Random()).nextInt(2000));
  }

  @Override
  public final String echo(final String msg) throws RemoteException {
    return msg;
  }

  @Override
  public final ResourceIdentifier getResourceUri() throws RemoteException {
    throw new RemoteException("getResourceUri");
  }
}
