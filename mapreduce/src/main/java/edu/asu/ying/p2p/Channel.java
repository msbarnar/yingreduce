package edu.asu.ying.p2p;

import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.MessageOutputStream;

/**
 * A {@code Channel} provides a single point of access for input from and output to the underlying
 * network.
 */
public interface Channel {

  void registerMessageHandler(final MessageHandler handler, final String tag);

  MessageOutputStream getMessageOutputStream();
}
