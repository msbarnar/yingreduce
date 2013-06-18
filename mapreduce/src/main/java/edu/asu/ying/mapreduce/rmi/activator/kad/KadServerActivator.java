package edu.asu.ying.mapreduce.rmi.activator.kad;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import edu.asu.ying.mapreduce.net.kad.KademliaNetwork;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;


/**
 * Controls creation and lifetime management for remotely activated objects.
 */
public final class KadServerActivator
    implements Activator {

  public static final class RemoteTestImpl implements RemoteTest {

    @Inject
    public RemoteTestImpl() {
    }

    @Override
    public String getString() throws RemoteException {
      return "Hello! This is only a test.";
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public final <T extends Remote> T getReference(final Class<T> type,
                                                 final Map<String, String> properties)
      throws RemoteException {
    // TODO: use UnicastRemoteObject.exportObject
    System.out.println(String.format("\nServer: Get Instance: %s", type.toString()));
    final Injector injector = Guice.createInjector(new KademliaNetwork());
    final Remote instance;
    instance = injector.getInstance(type);
    return (T) UnicastRemoteObject.exportObject(instance, 3334);
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
