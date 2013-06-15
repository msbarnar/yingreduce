package edu.asu.ying.mapreduce.channels.kad;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.net.NoResponseException;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.Node;


/**
 * {@link KadSendMessageStream} is a {@link MessageOutputStream} that serializes messages to remote nodes on the Kademlia
 * network.
 */
public final class KadSendMessageStream
	implements MessageOutputStream
{
	private final KeybasedRouting kadNode;
	private final ResourceIdentifier localUri;

	@Inject
	private KadSendMessageStream(final KeybasedRouting kadNode) throws URISyntaxException {
		this.kadNode = kadNode;
		this.localUri = this.makeLocalUri();
	}

	/**
	 * Returns the {@link ResourceIdentifier} associated with the local host.
	 * <p>
	 * The node identifier format is as follows:
	 * <p>
	 * {@code node/node-key"}
	 */
	private final ResourceIdentifier makeLocalUri() throws URISyntaxException {
		return new ResourceIdentifier("node", this.kadNode.getLocalNode().getKey().toBase64());
	}

	/**
	 * Sends a message to the host identified on {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()}.
	 * @param message the message to send, complete with the destination URI.
	 * @throws IOException if the host is unable to be located or sending the message fails.
	 */
	@Override
	public final void write(final Message message) throws IOException {
		try {
			message.setSourceUri(new ResourceIdentifier(message.getDestinationUri().getScheme(),
		                                            this.localUri.getAddress()));
		} catch (final URISyntaxException e) {
			throw new IOException(e);
		}
		// Identify the destination nodes and send the message to them
		// This line just decided what charset keys are in forever
		final String host = message.getDestinationUri().getHost();
		final Key destKey = new Key(message.getDestinationUri().getHost());

		final List<Node> foundNodes = this.kadNode.findNode(destKey);
		if (foundNodes.size() == 0) {
			// This should never happen
			throw new UnknownHostException();
		}

		final String scheme = message.getDestinationUri().getScheme();
		// Send the message to the k nearest nodes (defined on the message's replication property)
		final Iterator<Node> iter = foundNodes.iterator();
		for (int i = 0; iter.hasNext() && (i < message.getReplication()); i++) {
			this.kadNode.sendMessage(iter.next(), scheme, message);
		}
	}

	/**
	 * Sends a message to the hosts identified on {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()},
	 * and gets verification of reception from the remote hosts.
	 * <p>
	 * The verification message from each host will be {@link edu.asu.ying.mapreduce.messaging.AcknowledgementMessage}
	 * if the message is received, or {@link edu.asu.ying.mapreduce.messaging.ExceptionMessage} signaling the problem
	 * if not.
	 * <p>
	 * Use the {@link edu.asu.ying.mapreduce.messaging.Message#getSourceUri()} property of each response to identify
	 * which hosts responded.
	 * @param message the message to send, complete with the destination URI.
	 * @return the verification of reception returned on the remote hosts.
	 * @throws IOException if the host is unable to be located, sending the message fails, or a timely response is not
	 * received.
	 */
	public final List<Message> writeWithVerification(final Message message) throws IOException {
		// Identify the destination nodes and send the message to them
		// This line just decided what charset IDs are in forever
		final Key destKey = new Key(message.getId().getBytes(Charsets.UTF_8));

		final List<Node> foundNodes = this.kadNode.findNode(destKey);
		if (foundNodes.size() == 0) {
			// This should never happen
			throw new UnknownHostException();
		}

		// Collect future responses on destination URI
		final Map<ResourceIdentifier, Future<Serializable>> responses = new HashMap<>();

		// Send the message to the k nearest nodes (defined on the message's replication property)
		final Iterator<Node> iter = foundNodes.iterator();
		for (int i = 0; iter.hasNext() && (i < message.getReplication()); i++) {
			// TODO: un-hardcode the openKad tag
			responses.put(message.getDestinationUri(), this.kadNode.sendRequest(iter.next(), "mapreduce", message));
		}

		// Collect response messages
		final List<Message> ret = new ArrayList<>();
		for (final Map.Entry<ResourceIdentifier, Future<Serializable>> response : responses.entrySet()) {
			Message result;
			try {
				result = (Message) response.getValue().get();
			} catch (final InterruptedException | ExecutionException e) {
				// Didn't get a response
				result = new ExceptionMessage(new NoResponseException(e));
				result.setSourceUri(response.getKey());
			} catch (final ClassCastException e) {
				// Response did not derive from Message
				// Make the message appear to have come from the remote node
				result = new ExceptionMessage(e);
				result.setSourceUri(response.getKey());
			}
			ret.add(result);
		}

		return ret;
	}
}
