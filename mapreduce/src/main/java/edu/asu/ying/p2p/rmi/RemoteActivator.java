package edu.asu.ying.p2p.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * {@code RemoteActivator} is the public interface to the {@link Activator}, which provides {@link
 * java.rmi.Remote} proxy references to local objects.
 */
public interface RemoteActivator extends Remote, Serializable {

  public <T extends Remote> T getReference(final Class<?> cls) throws RemoteException;
}
