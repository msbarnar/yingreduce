package edu.asu.ying.mapreduce.rpc.channels;

import java.net.URI;
import java.net.URISyntaxException;

import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;
import edu.asu.ying.mapreduce.rpc.net.NodeNotFoundException;

public interface SendChannelTransportSink
	extends MessageSink, ChannelTransportSink
{
	public void join(final URI uri) throws URISyntaxException, NodeNotFoundException;
}
