package edu.asu.ying.mapreduce.channels.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import edu.asu.ying.mapreduce.channels.SendChannelSink;
import edu.asu.ying.mapreduce.messaging.MessageSink;
import edu.asu.ying.mapreduce.messaging.SendMessageSink;
import edu.asu.ying.mapreduce.messaging.kad.KadSendMessageSink;


/**
 * The {@link KadChannel} constructs the message and channel sink chains necessary to connect high-level peer
 * operations (e.g. {@link edu.asu.ying.mapreduce.rmi.finder.ResourceFinder} to the underlying Kademlia network stack.
 */
public final class KadChannel
	extends AbstractModule
{
	@Override
	protected void configure() {
	}

	@Provides
	private final SendChannelSink provideSendChannelSink() {
		return new KadSendChannelSink();
	}
	/**
	 * Hooks up a {@link KadSendMessageSink} to a {@link KadSendChannelSink} so that messages are sent out to the
	 * Kademlia stack.
	 */
	@Provides @SendMessageSink
	private final MessageSink provideSendMessageSink(final SendChannelSink sendChannelSink) {
		return new KadSendMessageSink(sendChannelSink);
	}
}
