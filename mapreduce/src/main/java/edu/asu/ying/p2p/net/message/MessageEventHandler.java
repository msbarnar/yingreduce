package edu.asu.ying.p2p.net.message;

/**
 *
 */
public interface MessageEventHandler {

  boolean onMessage(final Message message);

  Message onRequest(final Message request);
}
