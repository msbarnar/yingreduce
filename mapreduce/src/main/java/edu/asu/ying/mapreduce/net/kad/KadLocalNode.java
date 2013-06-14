package edu.asu.ying.mapreduce.net.kad;

import edu.asu.ying.mapreduce.messaging.MessageDispatch;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.LocalNode;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.MessageHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;


/**
 * Provides an interface to the local Kademlia node.
 */
public final class KadLocalNode
	implements LocalNode
{
	private final KeybasedRouting kadNode;
	private final KadMessageHandler handler;
	private final MessageDispatch dispatch;

	@Inject
	public KadLocalNode(final KeybasedRouting kadNode, final KadMessageHandler handler,
	                    final MessageDispatch dispatch) {
		this.kadNode = kadNode;
		this.handler = handler;
		this.dispatch = dispatch;
	}

	@Override
	public final void join(final URI bootstrap) throws IOException {
	}

	public final MessageDispatch getMessageDispatch() { return this.dispatch; }
}
