package edu.asu.ying.mapreduce.rmi.activator;

import edu.asu.ying.mapreduce.net.resource.RemoteResource;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * {@link Activator} is the client node's entry point to remote method invocation on a server node.
 * <p>
 * The {@link Activator} is a remote interface to a {@link java.rmi.Remote} reference factory.
 * <br>The remote activator is responsible for instantiating objects in the remote domain and returning 
 * remote references (proxy classes) to the caller.
 * <p>
 * This is analogous to the {@link java.rmi.registry.Registry}, but depends on injected factories on the
 * remote side to provide the objects instead of maintaining a JVM-global registry.
 * <p>
 * The activator is more similar to Microsoft's .NET Remoting Activator in that the server-side activator
 * implementation draws its references from factories that may produce objects on the following bases:
 * <ul>
 * <li>Singleton</li>
 * <li>Client-Activated (isActive for the lifetime of the remote reference)</li>
 * <li>Single-Call (one instance per method invocation)</li>
 * </ul>
 * <p>
 * {@link java.rmi.Remote} references are produced, for example, using 
 * {@link java.rmi.server.UnicastRemoteObject#exportObject(Remote)}, but the actual method is implementation
 * specific.
 * <p>
 * {@link Activator} is a {@link RemoteResource} that can be located on a
 * {@link edu.asu.ying.mapreduce.net.resource.ResourceIdentifier} where the {@code scheme} is {@code activator} and the
 * {@code host} is the implementation-specific host identifier.
 * <br>The {@code authority} part may be used on the implementing activator to apply authorization rules to
 * object provisioning. 
 */
public interface Activator
	extends RemoteResource
{
	/**
	 * Obtains a {@link java.rmi.Remote} reference to an instance of the class {@code type}.
	 * @param type the class of the instance to obtain a reference to.
	 * @param properties may be used on the activator implementation when instantiating the object.
	 * @return a {@link java.rmi.Remote} reference to a remote object.
	 * @throws RemoteException if the remote {@link Activator} resource was unavailable or could not be
	 * found, or if a problem occurred while obtaining the reference.
	 */
	<T extends Remote> T getReference(final Class<T> type, final Map<String, String> properties)
			throws RemoteException;

	String echo(final String message) throws RemoteException;
}
