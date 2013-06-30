package edu.asu.ying.mapreduce.net.messaging;

import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;

/**
 *
 */
public interface MessageHandler {

  MessageHandler bind(final String scheme);

  FilteredValueEvent<Message> getIncomingMessageEvent();
}
