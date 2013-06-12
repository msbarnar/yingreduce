package edu.asu.ying.mapreduce.rmi.registry;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.google.inject.Inject;

import edu.asu.ying.mapreduce.rmi.ResourceIdentifier;

/**
 * {@link KadLocateRegistry} is used to obtain a reference to a remote {@link Registry}
 * on a node in a Kademlia network. The returned reference can be used to get references
 * to objects on that node.
 */
public final class KadLocateRegistry
{
	/**
	 * Uses the {@link LocateRegistry} to create and export a {@link Registry} instance on
	 * the local host that accepts requests on the specified port.
	 * @param port the {@link Registry} will accept requests on this port.
	 * @return a {@link Registry} on the local host that is listening on <code>port</code>.
	 * @throws RemoteException 
	 */
	public static Registry createRegistry(int port) throws RemoteException {
		return LocateRegistry.createRegistry(port);
	}
	
	/**
	 * Gets a reference to a {@link Registry} object on a remote Kademlia node
	 * identified by the key given in <code>url</code>.
	 * <p>
	 * The {@link ResourceIdentifier} is the key used to look up the Kademlia node
	 * on which the {@link Registry} is instantiated.
	 * @param url the Kademlia key 
	 * @return a reference to the {@link Registry} instance on a remote Kademlia node.
	 */
	@Inject
	public static Registry getRegistry(final ResourceIdentifier nodeKey) 
			throws RemoteException {
		// Locate the Kademlia node referenced by the node key
		
		return null;
	}
}
