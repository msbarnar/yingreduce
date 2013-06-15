package edu.asu.ying.mapreduce.messaging;

/**
 * A {@link FilteredMessageCallback} is a target of message callbacks from {@link FilteredMessageHandler}.
 */
public interface FilteredMessageCallback
{
	void accept(final Message message);
}
