package edu.asu.ying.mapreduce.messaging.kad;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import edu.asu.ying.mapreduce.channels.ChannelSink;
import edu.asu.ying.mapreduce.channels.SendChannelSink;
import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.messaging.MessageSink;
import edu.asu.ying.mapreduce.messaging.SendMessageSink;

import java.io.Serializable;


/**
 * The {@link KadSendMessageSink} passes messages to a {@link SendChannelSink} for sending to a remote host.
 * <p>
 * The purpose of chaining the send message sink to an abstract channel sink is to allow the message to be verified and
 * manipulated before being passed into the Kademlia channel.
 */
public final class KadSendMessageSink
	implements MessageSink
{
	private final SendChannelSink nextSink;

	@Inject
	public KadSendMessageSink(final SendChannelSink nextSink) {
		this.nextSink = nextSink;
	}

	/**
	 * The {@link SendMessageSink} is the last in the message sink chain.
	 * @return null
	 */
	@Override
	public final MessageSink getNextSink() { return null; }

	public final SendChannelSink getNextChannelSink() { return this.nextSink; }

	/**
	 * Passes the message to the underlying channel sink and
	 * @param message
	 * @return
	 */
	@Override
	public final Message accept(final Message message) {
		final Serializable response = this.nextSink.sendMessage(message.getDestinationUri(), message);
		if (!(response instanceof Message)) {
			throw new IllegalArgumentException(
					"Response from channel sink was not a message: ".concat(response.getClass().toString()));
		}

		return (Message) response;
	}
}
