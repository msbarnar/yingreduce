package edu.asu.ying.mapreduce.net.kad;

import edu.asu.ying.mapreduce.messaging.MessageDispatch;
import edu.asu.ying.mapreduce.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.rmi.activator.ActivatorProvider;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;
import il.technion.ewolf.kbr.KeybasedRouting;

import javax.inject.Inject;
import java.io.IOException;


/**
 * Provides an interface to the local Kademlia node.
 */
public final class KadLocalNode
	implements LocalNode
{
	private final KeybasedRouting kadNode;
	private final KadMessageHandler handler;
	private final MessageDispatch dispatch;
	private final ActivatorProvider activatorProvider;

	@Inject
	public KadLocalNode(final KeybasedRouting kadNode, final KadMessageHandler handler,
	                    final MessageDispatch dispatch, final ActivatorProvider activatorProvider) {
		this.kadNode = kadNode;
		this.handler = handler;
		this.dispatch = dispatch;
		this.activatorProvider = activatorProvider;
	}

	@Override
	public final void join(final ResourceIdentifier bootstrap) throws IOException {
	}

	public final MessageDispatch getMessageDispatch() { return this.dispatch; }
}
