package edu.asu.ying.mapreduce.node.io;

import edu.asu.ying.mapreduce.node.io.MessageEventHandler;
import edu.asu.ying.mapreduce.node.io.message.Message;

/**
 *
 */
public final class MessageRequestEvent extends RequestEvent<MessageEventHandler, Message> {

  public MessageRequestEvent() {
  }

  @Override
  protected boolean fireEvent(MessageEventHandler handler, Object sender,
                              Message message) {

    return handler.onMessage(message);
  }

  @Override
  protected Object fireRequest(MessageEventHandler handler, Object sender,
                               Message request) {

    return handler.onRequest(request);
  }
}
