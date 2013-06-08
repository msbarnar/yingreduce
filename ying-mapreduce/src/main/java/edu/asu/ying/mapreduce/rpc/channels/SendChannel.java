package edu.asu.ying.mapreduce.rpc.channels;

import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;

/**
 * Base interface for a channel that sends data.
 */
public interface SendChannel
	extends Channel
{
	public SendChannelTransportSink getTransportSink();
	public MessageSink getMessageSink();
}
