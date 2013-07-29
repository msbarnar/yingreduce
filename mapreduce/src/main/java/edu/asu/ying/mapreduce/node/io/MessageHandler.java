package edu.asu.ying.mapreduce.node.io;

import edu.asu.ying.mapreduce.node.io.message.Message;

/**
 *
 */
public interface MessageHandler {

  String getTag();
  void onIncomingMessage(final Message message);
  Message onIncomingRequest(final Message request);
}
