package edu.asu.ying.mapreduce.net;

import java.io.IOException;
import java.net.URI;


/**
 * Provides an interface to the local Kademlia node and its listening facilities.
 */
public interface LocalNode
{
	void join(final URI bootstrap) throws IOException;
}
