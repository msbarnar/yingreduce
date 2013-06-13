package edu.asu.ying.mapreduce.channels.kad;

import edu.asu.ying.mapreduce.channels.SendChannelSink;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.MessageSink;

import java.io.Serializable;


/**
 * The {@link KadSendChannelSink} is the last sink in an outgoing message sink chain and passes its messages to an
 * underlying Kademlia network stack.
 * <p>
 * Messages are sent by interpreting the {@code host} part of the destination URI as the Kademlia key of a remote
 * node.
 * <p>
 * In a blocking send, the sink waits for a response from the Kademlia stack until it gets one or times out.
 */
public final class KadSendChannelSink
	implements SendChannelSink
{
	/**
	 * Serializes the message to the underlying Kademlia stack, sending it to the node specified by
	 * {@link edu.asu.ying.mapreduce.messaging.Message#getDestinationUri()}.
	 * @param message the message to be sent
	 */
	@Override
	public final void write(final Serializable message) {
	}
}
