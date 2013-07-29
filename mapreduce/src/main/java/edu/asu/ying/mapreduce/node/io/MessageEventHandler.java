package edu.asu.ying.mapreduce.node.io;

import edu.asu.ying.mapreduce.node.io.message.Message;

/**
 *
 */
public interface MessageEventHandler {

  boolean onMessage(final Message message);
  Message onRequest(final Message request);
}
