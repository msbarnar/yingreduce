package edu.asu.ying.mapreduce.rpc.net.kad;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import il.technion.ewolf.kbr.KeybasedRouting;

/**
 * Provides the interface for a class that provides instances of {@link KeybasedRouting}.
 */
public interface KeybasedRoutingProvider
{
	/**
	 * Returns an instance of the {@link KeybasedRouting} kademlia node.
	 */
	public KeybasedRouting getKeybasedRouting();
	/**
	 * Makes a kademlia-compatible URI.
	 * @param uri the URI to transform. Necessary components are the host and the port. 
	 * @return a URI compatible with {@link KeybasedRouting#join}.
	 * @throws URISyntaxException
	 */
	public URI makeURI(final URI uri) throws URISyntaxException;
	/**
	 * Makes a kademlia-compatible URI.
	 * @param address the address of the remote node.
	 * @param port the port on which the remote node has a {@link KeybasedRouting} instance
	 * listening. 
	 * @return a URI compatible with {@link KeybasedRouting#join}.
	 * @throws URISyntaxException
	 */
	public URI makeURI(final InetAddress address, final int port) throws URISyntaxException;
}
