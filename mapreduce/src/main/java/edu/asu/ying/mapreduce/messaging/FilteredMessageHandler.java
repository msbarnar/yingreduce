package edu.asu.ying.mapreduce.messaging;

import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;


/**
 * A {@code FilteredMessageHandler} receives all messages from the network and filters them to registered callbacks.
 */
public interface FilteredMessageHandler
{
	void addCallback(final MessageFilter filter, final FilteredMessageCallback callback);
}
