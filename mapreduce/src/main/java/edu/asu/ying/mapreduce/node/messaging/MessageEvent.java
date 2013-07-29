package edu.asu.ying.mapreduce.node.messaging;

import edu.asu.ying.mapreduce.common.event.Event;

/**
 *
 */
public final class MessageEvent extends Event<MessageEventHandler, Message> {

  public MessageEvent() {
  }

  @Override
  protected boolean fireHandler(MessageEventHandler handler, Object sender,
                                Message message) {

    return handler.onMessage(message);
  }

  @Override
  protected Object requestHandler(MessageEventHandler handler, Object sender,
                                  Message request) {

    return handler.onRequest(request);
  }
}
