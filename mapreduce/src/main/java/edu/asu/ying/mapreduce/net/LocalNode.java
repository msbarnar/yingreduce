package edu.asu.ying.mapreduce.net;

import java.io.IOException;
import java.util.List;

import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.rmi.Activator;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode extends Node {

  void bind();

  void join(final ResourceIdentifier bootstrap) throws IOException;

  MessageHandler getMessageHandler(final String scheme);

  Activator getActivator();

  List<RemoteNode> getNeighbors();
}
