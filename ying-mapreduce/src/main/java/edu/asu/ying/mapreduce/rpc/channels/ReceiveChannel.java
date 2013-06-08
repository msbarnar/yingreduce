package edu.asu.ying.mapreduce.rpc.channels;

import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;

public interface ReceiveChannel
	extends Channel
{
	public ReceiveChannelTransportSink getTransportSink();
	public MessageSink getRequestSink();
	public MessageSink getResponseSink();
}
