package edu.asu.ying.p2p.message;

/**
 *
 */
public interface MessageHandler {

  void onIncomingMessage(final Message message);

  Message onIncomingRequest(final Message request);
}
