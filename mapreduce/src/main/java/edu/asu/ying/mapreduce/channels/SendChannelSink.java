package edu.asu.ying.mapreduce.channels;

import edu.asu.ying.mapreduce.messaging.MessageSink;

import java.io.Serializable;
import java.net.URI;


/**
 * A {@link SendChannelSink} is the final sink in an outgoing message sink chain. The {@link SendChannelSink} is
 * responsible for serializing the message to the underlying network stack.
 */
public interface SendChannelSink
	extends ChannelSink
{
	public void write(final URI destination, final Serializable message);
}
