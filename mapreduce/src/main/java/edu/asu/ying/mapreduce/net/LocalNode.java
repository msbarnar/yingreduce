package edu.asu.ying.mapreduce.net;

import java.io.IOException;

import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;
import edu.asu.ying.mapreduce.rmi.Activator;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode {

  void bind();

  void join(final ResourceIdentifier bootstrap) throws IOException;

  MessageHandler getMessageHandler(final String scheme);

  Activator getActivator();
}
