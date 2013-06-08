package edu.asu.ying.mapreduce.rpc.channels.kad;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.rpc.channels.ReceiveChannelTransportSink;
import edu.asu.ying.mapreduce.rpc.messaging.*;
import edu.asu.ying.mapreduce.rpc.net.NodeNotFoundException;
import edu.asu.ying.mapreduce.rpc.net.kad.KeybasedRoutingProvider;
import edu.asu.ying.mapreduce.rpc.net.kad.SingletonKeybasedRoutingProvider;

import il.technion.ewolf.kbr.*;

/**
 * Listens to the Kademlia network for messages destined for this node
 * and passes them to a {@link ReceiveChannelSink} chain.
 */
public final class KadReceiveTransportSink 
	implements MessageHandler, ReceiveChannelTransportSink
{
	private final Map<String, Object> properties = new HashMap<String, Object>();
	
	// The Kad endpoint
	private final KeybasedRouting kbrNode;
	// The sink that will receive requests from this transport sink.
	private final MessageSink requestSink;
	// The sink that will have the opportunity to modify responses on their way out
	private final MessageSink responseSink;
	
	/**
	 * Initialize the local node, but don't attempt to join any network
	 * (server-only mode).
	 */
	public KadReceiveTransportSink(final MessageSink requestSink, final MessageSink responseSink) {
		this.requestSink = requestSink;
		this.responseSink = responseSink;
		this.kbrNode = (new SingletonKeybasedRoutingProvider()).getKeybasedRouting();
		this.registerKadHandlers();
		
		try {
			this.properties.put("port", this.kbrNode.getLocalNode().getPort("openkad.udp"));
		} catch (final NullPointerException e) {
			this.properties.put("port", -1);
		}
	}
	/**
	 * Initialize the local node and attempt to join an existing network.
	 * @param remoteNodeAddress the address of a node in the existing network.
	 * @throws URISyntaxException
	 * @throws NodeNotFoundException
	 */
	public KadReceiveTransportSink(final MessageSink requestSink, final MessageSink responseSink,
	                              final URI remoteNodeAddress) 
			throws URISyntaxException, NodeNotFoundException {
		this.requestSink = requestSink;
		this.responseSink = responseSink;
		// Get an instance of the Kademlia node
		final KeybasedRoutingProvider kbrProvider = new SingletonKeybasedRoutingProvider();
		this.kbrNode = kbrProvider.getKeybasedRouting();
		// Register this class to receive messages from the network
		this.registerKadHandlers();
		if (remoteNodeAddress != null) {
			// Join an existing network
			this.join(kbrProvider, remoteNodeAddress);
		}
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
	}
	/**
	 * Registers this class to receive messages and requests from {@link KeybasedRouting}.
	 */
	private final void registerKadHandlers() {
		this.kbrNode.register("mapreduce", this);
	}
	
	/*************************************************
	 * Kad MessageHandler implementation
	 */
	
	/**
	 * Receives a unidirectional message from another node and passes it to the
	 * {@link ServerFormatterSink} chain.
	 */
	@Override
	public final void onIncomingMessage(final Node from, final String tag, final Serializable content) {
		final Message message = (Message) content;
		message.getProperties().put("kad:source-node", from);
		message.getProperties().put("kad:tag", tag);
		this.requestSink.processMessage(message);
	}
	
	/**
	 * Receives a request for response from another node and passes it to the
	 * {@link ReceiveChannelSink} chain, returning the response given by the chain.
	 */
	@Override
	public final Serializable onIncomingRequest(final Node from, final String tag, final Serializable content) {
		// Set up the request headers and pass the request to a server channel sink
		final Message message = (Message) content;
		message.getProperties().put("kad:source-node", from);
		message.getProperties().put("kad:tag", tag);
		// Get the response from the server channel sink and chain it into a client
		// channel sink. The return value from the client channel should be a formatted
		// response.
		final Message response = this.requestSink.processMessage(message);
		return this.responseSink.processMessage(response);
	}
	
	@Override
	public void close() {
		this.kbrNode.shutdown();
	}
	
	@Override
	public final Map<String, Object> getProperties() { return this.properties; }
}
