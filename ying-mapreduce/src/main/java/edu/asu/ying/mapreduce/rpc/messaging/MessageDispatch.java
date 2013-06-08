package edu.asu.ying.mapreduce.rpc.messaging;

import java.util.*;

public final class MessageDispatch
	implements MessageSink
{
	private final Map<Class<? extends Message>, MessageSink> messageSinks = new HashMap<Class<? extends Message>, MessageSink>();
	
	/**
	 * Returns null.
	 * <p>
	 * {@link MessageDispatch} has a map of "next" sinks based on what type of messages come in.
	 */
	@Override
	public MessageSink getNextMessageSink() {
		return null;
	}

	/**
	 * If any message sink is associated with the type of message received,
	 * {@link MessageDispatch#processMessage} passes the message to it.
	 * @return the result from the sink, or null if no sink is registered.
	 */
	@Override
	public Message processMessage(final Message message) {
		final MessageSink sink = this.messageSinks.get(message.getClass());
		if (sink != null) {
			return sink.processMessage(message);
		}
		return null; 
	}
	
	/**
	 * Allows a {@link MessageSink} to be registered to receive messages of a certain type.
	 * @param messageClass the class of messages the message sink wants to receive
	 * @param sink the message sink to receive messages
	 */
	public void registerSink(final Class<? extends Message> messageClass, final MessageSink sink) {
		this.messageSinks.put(messageClass, sink);
	}
}
