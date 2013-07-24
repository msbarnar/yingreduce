package edu.asu.ying.mapreduce.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * {@link Activator} is the client node's entry point to remote method invocation on a scheduling
 * node.
 * </p>
 * The {@link Activator} is a remote interface to a {@link java.rmi.Remote} reference factory.
 * </br>The remote remote is responsible for instantiating objects in the remote domain and
 * returning remote references (proxy classes) to the caller.
 * </p>
 * This is analogous to the {@link java.rmi.registry.Registry}, but depends on injected factories
 * on the remote side to provide the objects instead of maintaining a JVM-global registry.
 * </p>
 * The remote is more similar to Microsoft's .NET Remoting Activator in that the scheduling-side
 * remote implementation draws its references from factories that may produce objects on the
 * following bases:
 * <ul>
 *   <li>Singleton</li>
 *   <li>Client-Activated (active for the lifetime of the remote reference)</li>
 *   <li>Single-Call (one instance per method invocation)</li>
 * </ul>
 * </p>
 * {@link java.rmi.Remote} references are produced, for example,
 * using {@link java.rmi.server.UnicastRemoteObject#exportObject(Remote)}, but the actual method is
 * implementation specific.
 */
public interface Activator extends Remote, Serializable {

  /**
   * Obtains a {@link java.rmi.Remote} reference to an instance of the class {@code type}.
   *
   * @param type       the class of the instance to obtain a reference to.
   * @param properties may be used on the remote implementation when instantiating the object.
   * @return a {@link java.rmi.Remote} reference to the instance.
   */
  <T extends Remote> T export(final Class<T> type, final Map<String, String> properties)
      throws RemoteException;

  /**
   * Obtains a {@link java.rmi.Remote} reference to the specific instance {@code instance}.
   *
   * @param instance       the instance to reference.
   * @param properties may be used on the remote implementation when creating the reference.
   * @return a {@link java.rmi.Remote} reference to the instance.
   */
  <T extends Remote> T export(final T instance, final Map<String, String> properties)
      throws RemoteException;
}
