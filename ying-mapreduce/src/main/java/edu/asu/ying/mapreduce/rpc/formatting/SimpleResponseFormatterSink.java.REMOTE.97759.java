package edu.asu.ying.mapreduce.rpc.formatting;

import edu.asu.ying.mapreduce.rpc.messaging.*;

/**
 * Formats responses and returns them directly to the caller.
 */
public final class SimpleResponseFormatterSink
	implements MessageSink
{
	public SimpleResponseFormatterSink() {
	}
	

	@Override
	public final Message processMessage(final Message message) {
		return message;
	}

	@Override
	public final MessageSink getNextMessageSink() {
		return null;
	}
}
