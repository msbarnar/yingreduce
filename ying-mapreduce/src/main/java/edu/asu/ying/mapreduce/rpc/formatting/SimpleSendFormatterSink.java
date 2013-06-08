package edu.asu.ying.mapreduce.rpc.formatting;

import java.io.IOException;

import edu.asu.ying.mapreduce.rpc.messaging.Message;
import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;

/**
 * Provides an implementation of {@link ClientFormatterSink} that passes Serializable
 * messages as-is.
 */
public class SimpleSendFormatterSink
	implements MessageSink
{
	private final MessageSink nextSink;
	
	public SimpleSendFormatterSink(final MessageSink nextSink) {
		this.nextSink = nextSink;
	}
	
	/*
	 * MessageSink
	 */
	
	/**
	 * Forwards the message directly to the next sink without formatting it.
	 * @throws IOException 
	 */
	@Override
	public Message processMessage(final Message msg) {
		return this.nextSink.processMessage(msg);
	}
	
	/**
	 * Formatters are the last sink in the message sink chain.
	 * @return null.
	 */
	@Override
	public final MessageSink getNextMessageSink() {
		return this.nextSink;
	}
}
