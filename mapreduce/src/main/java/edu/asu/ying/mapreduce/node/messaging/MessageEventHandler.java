package edu.asu.ying.mapreduce.node.messaging;

/**
 *
 */
public interface MessageEventHandler {

  boolean onMessage(final Message message);
  Message onRequest(final Message request);
}
