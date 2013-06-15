package edu.asu.ying.mapreduce.net;

import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;

import java.io.IOException;


/**
 * Provides an interface to the local Kademlia node and its listening facilities.
 */
public interface LocalNode
{
	void join(final ResourceIdentifier bootstrap) throws IOException;
}
