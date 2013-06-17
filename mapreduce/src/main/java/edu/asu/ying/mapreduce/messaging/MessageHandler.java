package edu.asu.ying.mapreduce.messaging;

import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;

/**
 *
 */
public interface MessageHandler
{
	void bind(final String scheme);
	FilteredValueEvent<Message> getIncomingMessageEvent();
}
