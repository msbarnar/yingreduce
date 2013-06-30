package edu.asu.ying.mapreduce.net.messaging;

import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;

/**
 *
 */
public interface MessageHandler {

  MessageHandler bind(final String scheme);

  FilteredValueEvent<Message> getIncomingMessageEvent();
}
