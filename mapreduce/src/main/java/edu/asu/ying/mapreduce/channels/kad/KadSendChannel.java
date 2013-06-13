package edu.asu.ying.mapreduce.channels.kad;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.MessageOutputStream;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.Node;

import javax.swing.text.html.HTMLDocument;


/**
 * {@link KadSendChannel} is a {@link MessageOutputStream} that serializes messages to remote nodes on the Kademlia
 * network.
 */
public final class KadSendChannel
	implements MessageOutputStream
{
	private final KeybasedRouting kadNode;

	@Inject
	public KadSendChannel(final KeybasedRouting kadNode) {
		this.kadNode = kadNode;
	}

	@Override
	public final void write(final Message message) throws IOException {
		// Identify the destination nodes and send the message to them
		// This line just decided what charset IDs are in forever
		final Key destKey = new Key(message.getId().getBytes(Charsets.UTF_8));

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
}
