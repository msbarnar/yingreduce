package edu.asu.ying.mapreduce.net.kad;

import edu.asu.ying.mapreduce.net.LocalNode;
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

	@Inject
	private KadLocalNode(final KeybasedRouting kadNode) {
		this.kadNode = kadNode;
	}

	@Override
	public final void join(final ResourceIdentifier bootstrap) throws IOException {
	}
}
