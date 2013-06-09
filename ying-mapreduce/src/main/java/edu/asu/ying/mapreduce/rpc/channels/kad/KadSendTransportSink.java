package edu.asu.ying.mapreduce.rpc.channels.kad;

import il.technion.ewolf.kbr.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import edu.asu.ying.mapreduce.rpc.channels.SendChannelTransportSink;
import edu.asu.ying.mapreduce.rpc.messaging.*;
import edu.asu.ying.mapreduce.rpc.net.*;
import edu.asu.ying.mapreduce.rpc.net.kad.*;
import edu.asu.ying.mapreduce.ui.ObservableProperties;

/**
 * {@link KadSendTransportSink} is the client interface to the Kademlia network.
 * <p>
 * The {@link KadSendTransportSink} accepts formatted messages from a {@link ClientFormatterSink} 
 * (optionally through a {@link SendChannelSink} chain) and hands them to the local Kademlia node for 
 * transmission.
 * <p>
 * Messages must have associated {@link TransportHeaders} which define routing information for the message.
 * <p>
 * If the {@link TransportHeaders} specify a request vs. a one-off message, the client channel sends the request
 * and returns an asynchronous result from the remote node. 
 */
public final class KadSendTransportSink
	implements MessageSink, SendChannelTransportSink
{
	private final Map<String, Object> properties = new HashMap<String, Object>();
	private final ObservableProperties exposedProps = new ObservableProperties();
	
	// The Kad endpoint
	private final KeybasedRouting kbrNode;
	
	/**
	 * Initialize the transport sink with the address of a remote bootstrap node
	 * into the network.
	 * @param remoteNodeAddress the address of a remote node in the network.
	 * @throws URISyntaxException if the remote node address is not a valid URI.
	 * @throws NodeNotFoundException 
	 */
	public KadSendTransportSink(final URI remoteNodeAddress) 
			throws URISyntaxException, NodeNotFoundException {
		// Get an instance of the Kademlia node
		final KeybasedRoutingProvider kbrProvider = new SingletonKeybasedRoutingProvider();
		this.kbrNode = kbrProvider.getKeybasedRouting();
		// Join an existing network in a send-only mode
		this.join(kbrProvider, remoteNodeAddress);
	}
	/**
	 * Joins the kademlia network specified by <code>remoteNodeAddress</code> using
	 * the kademlia node provided by the {@link KeybasedRoutingProvider}.
	 * @param kbrProvider provides the kademlia node instance that will connect to the network.
	 * @param remoteNodeAddress the address of a node in the network to connect to.
	 * @throws URISyntaxException
	 * @throws NodeNotFoundException
	 */
	private final void join(final KeybasedRoutingProvider kbrProvider, final URI remoteNodeAddress) 
			throws URISyntaxException, NodeNotFoundException {
		// Connect to a network
		final URI remoteUri = kbrProvider.makeURI(remoteNodeAddress);
		try {
			this.kbrNode.join(Arrays.asList(remoteUri));
		} catch (final IllegalStateException e) {
			throw new NodeNotFoundException(e);
		}
		
		this.exposedProps.clear();
		// Expose the bootstrap node address
		this.exposedProps.add(new AbstractMap.SimpleEntry<String, Serializable>(
				"bootstrap-address", remoteNodeAddress));
	}

	/**
	 * Sends the serialized message to the network according to the routing
	 * information in the given headers.
	 * @param msg the message to send
	 * @param messageHeaders the headers describing the message, including routing information.
	 * @throws IOException 
	 */
	@Override
	public Message processMessage(final Message message) {
		// Find destination nodes from key in headers
		final NetworkKey key = message.getNetworkKey();
		if (key == null) {
			throw new InvalidMessageException(message, 
			                                  "Message does not have network key attached and cannot be routed on the network.", 
			                                  new InvalidKeyException());
		}
		final List<Node> foundNodes = this.kbrNode.findNode(new Key(key.toByteArray()));
		// This should not happen. Kademlia returns the nearest nodes to the key, so the only
		// way to get zero nodes for a key is a bug in the local node.
		if (foundNodes.size() == 0) {
			throw new NodeNotFoundException(key);
		}
		// Send request to three nearest nodes
		final Iterator<Node> iter = foundNodes.iterator();
		for (int i = 0; i < 3; i++) {
			if (iter.hasNext()) {
				// Let the channel serialize the message
				try {
					this.kbrNode.sendMessage(iter.next(), "mapreduce", message);
				} catch (final IOException e) {
					throw new NetworkException("Failed to send message to peer", e);
				}
			} else {
				break;
			}
		}
		
		return null;
	}
	
	/**
	 * The transport sink is the end of the message sink chain.
	 */
	@Override
	public MessageSink getNextMessageSink() {
		return null;
	}
	
	@Override
	public void close() {
		this.kbrNode.shutdown();
	}
	
	@Override
	public final ObservableProperties getExposedProps() {
		return this.exposedProps;
	}
	
	@Override
	public final Map<String, Object> getProperties() { return this.properties; }
}
