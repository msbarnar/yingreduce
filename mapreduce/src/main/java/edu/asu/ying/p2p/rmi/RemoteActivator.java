package edu.asu.ying.p2p.rmi;

import java.io.Serializable;
import java.rmi.Remote;

/**
 * {@code RemoteActivator} is the public interface to the {@link Activator}, which provides {@link
 * java.rmi.Remote} proxy references to local objects.
 */
public interface RemoteActivator extends Remote, Serializable {

  public <T> T getReference(final Class<? super T> cls);
}
