package edu.asu.ying.p2p.io;

import edu.asu.ying.p2p.io.message.Message;

/**
 *
 */
public interface MessageEventHandler {

  boolean onMessage(final Message message);
  Message onRequest(final Message request);
}
