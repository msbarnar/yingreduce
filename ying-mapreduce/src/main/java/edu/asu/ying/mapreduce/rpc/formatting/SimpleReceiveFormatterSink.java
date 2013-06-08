package edu.asu.ying.mapreduce.rpc.formatting;

import edu.asu.ying.mapreduce.rpc.messaging.*;

/**
 * Accepts formatted messages from the transport sink chain and passes them as-is
 * to the server channel chain.
 * <p>
 * {@link SimpleReceiveFormatterSink} relies on the transport layer directly deserializing
 * {@link Serializable} objects from the network.
 */
public final class SimpleReceiveFormatterSink
	implements MessageSink
{
	private final MessageSink nextSink;
	
	public SimpleReceiveFormatterSink(final MessageSink nextSink) {
		this.nextSink = nextSink;
	}
	
	/*************************************
	 * MessageSink implementation
	 */
	
	@Override
	public MessageSink getNextMessageSink() {
		return this.nextSink;
	}
	
	/*************************************
	 * ChannelSink implementation
	 */
	
	@Override
	public final Message processMessage(final Message message) {
		try {
			return this.nextSink.processMessage(message);
		} catch (final Throwable e) {
			return new ResponseMessage(message, e);
		}
	}
}
