package edu.asu.ying.mapreduce.channels.kad;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.io.MessageOutputStream;
import edu.asu.ying.mapreduce.net.NoResponseException;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.Node;


/**
 * {@link KadSendChannel} is a {@link MessageOutputStream} that serializes messages to remote nodes on the Kademlia
 * network.
 */
public final class KadSendChannel
	implements MessageOutputStream
{
	private final KeybasedRouting kadNode;
	private final ResourceIdentifier localUri;

	@Inject
	public KadSendChannel(final KeybasedRouting kadNode) {
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
	private final ResourceIdentifier makeLocalUri() {
		return new ResourceIdentifier("node/".concat(this.kadNode.getLocalNode().getKey().toString()));
	}

	/**
	 * Sends a message to the host identified by {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()}.
	 * @param message the message to send, complete with the destination URI.
	 * @throws IOException if the host is unable to be located or sending the message fails.
	 */
	@Override
	public final void write(final Message message) throws IOException {
		message.setSourceUri(this.localUri);
		// Identify the destination nodes and send the message to them
		// This line just decided what charset keys are in forever
		final Key destKey = new Key(message.getDestinationUri().getHost().getBytes(Charsets.UTF_8));

		final List<Node> foundNodes = this.kadNode.findNode(destKey);
		if (foundNodes.size() == 0) {
			// This should never happen
			throw new UnknownHostException();
		}
		// Send the message to the k nearest nodes (defined by the message's replication property)
		final Iterator<Node> iter = foundNodes.iterator();
		for (int i = 0; iter.hasNext() && (i < message.getReplication()); i++) {
			// TODO: un-hardcode the openKad tag
			this.kadNode.sendMessage(iter.next(), "mapreduce", message);
		}
	}

	/**
	 * Sends a message to the hosts identified by {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()},
	 * and gets verification of reception from the remote hosts.
	 * <p>
	 * The verification message from each host will be {@link edu.asu.ying.mapreduce.messaging.AcknowledgementMessage}
	 * if the message is received, or {@link edu.asu.ying.mapreduce.messaging.ExceptionMessage} signaling the problem
	 * if not.
	 * <p>
	 * Use the {@link edu.asu.ying.mapreduce.messaging.Message#getSourceUri()} property of each response to identify
	 * which hosts responded.
	 * @param message the message to send, complete with the destination URI.
	 * @return the verification of reception returned by the remote hosts.
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

		// Collect future responses by destination URI
		final Map<ResourceIdentifier, Future<Serializable>> responses = new HashMap<>();

		// Send the message to the k nearest nodes (defined by the message's replication property)
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
