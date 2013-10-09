package edu.asu.ying.p2p.net;

/**
 * A {@code Channel} provides a single point of access for input from and output to the underlying
 * network.
 */
public interface Channel {

  void registerMessageHandler(final MessageHandler handler, final String tag);

  MessageOutputStream getMessageOutputStream();
}
