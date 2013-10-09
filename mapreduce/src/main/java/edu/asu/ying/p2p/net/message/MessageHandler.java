package edu.asu.ying.p2p.net.message;

/**
 *
 */
public interface MessageHandler {

  String getTag();

  void onIncomingMessage(final Message message);

  Message onIncomingRequest(final Message request);
}
