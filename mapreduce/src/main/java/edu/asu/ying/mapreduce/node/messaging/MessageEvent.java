package edu.asu.ying.mapreduce.node.messaging;

import edu.asu.ying.mapreduce.common.event.Event;
import edu.asu.ying.mapreduce.common.event.EventHandler;

/**
 *
 */
public class MessageEvent extends Event<EventHandler<Message>, Message> {

  public MessageEvent() {
  }
}
