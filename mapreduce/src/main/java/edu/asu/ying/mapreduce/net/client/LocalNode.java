package edu.asu.ying.mapreduce.net.client;

import edu.asu.ying.mapreduce.net.resource.ResourceIdentifier;

import java.io.IOException;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode
{
	void join(final ResourceIdentifier bootstrap) throws IOException;
}
