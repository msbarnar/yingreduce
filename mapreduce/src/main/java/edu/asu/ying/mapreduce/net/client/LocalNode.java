package edu.asu.ying.mapreduce.net.client;

import edu.asu.ying.mapreduce.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.activator.Activator;
import il.technion.ewolf.kbr.KeybasedRouting;

import java.io.IOException;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode
{
	void join(final ResourceIdentifier bootstrap) throws IOException;

	MessageHandler getMessageHandler(final String scheme);
	Activator getActivator();
}
