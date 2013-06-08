package edu.asu.ying.mapreduce.rpc.channels.kad;

import java.io.Serializable;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.rpc.channels.*;
import edu.asu.ying.mapreduce.rpc.formatting.*;
import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;
import edu.asu.ying.mapreduce.rpc.net.NodeNotFoundException;

public final class KadReceiveChannel
	implements ReceiveChannel
{
	private final Map<? extends Serializable, ? extends Serializable> properties = new HashMap<Serializable, Serializable>();
	
	private final SimpleReceiveFormatterSink formatterSink;
	private final SimpleResponseFormatterSink responseFormatterSink;
	private final ReceiveChannelTransportSink transportSink;
	
	public KadReceiveChannel(final MessageSink nextMessageSink) {
		this.formatterSink = new SimpleReceiveFormatterSink(nextMessageSink);
		this.responseFormatterSink = new SimpleResponseFormatterSink();
		this.transportSink = new KadReceiveTransportSink(this.formatterSink, this.responseFormatterSink);
	}
	public KadReceiveChannel(final MessageSink nextMessageSink, final URI remoteNodeAddress)
		throws URISyntaxException, NodeNotFoundException {
		this.formatterSink = new SimpleReceiveFormatterSink(nextMessageSink);
		this.responseFormatterSink = new SimpleResponseFormatterSink();
		this.transportSink = new KadReceiveTransportSink(this.formatterSink, 
		                                                this.responseFormatterSink,
		                                                remoteNodeAddress);
	}
	
	public void close() {
		this.transportSink.close();
	}
	
	@Override
	public final Map<? extends Serializable, ? extends Serializable> getProperties() { return this.properties; }
	
	@Override
	public final ReceiveChannelTransportSink getTransportSink() { return this.transportSink; }
	
	@Override
	public final MessageSink getRequestSink() { return this.formatterSink; }
	@Override
	public final MessageSink getResponseSink() { return this.responseFormatterSink; }
}
