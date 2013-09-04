package edu.asu.ying.p2p.io;

import edu.asu.ying.p2p.io.message.Message;

/**
 *
 */
public interface MessageHandler {

  String getTag();
  void onIncomingMessage(final Message message);
  Message onIncomingRequest(final Message request);
}
