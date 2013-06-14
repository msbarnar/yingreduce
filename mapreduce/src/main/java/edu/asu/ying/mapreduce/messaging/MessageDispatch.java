package edu.asu.ying.mapreduce.messaging;

import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;
import edu.asu.ying.mapreduce.messaging.io.MessageOutputStream;


/**
 * A {@link MessageDispatch} is a {@link edu.asu.ying.mapreduce.messaging.io.MessageOutputStream} that associates recipient objects with specific messages
 * so that they can be asynchronously notified when a message of interest arrives.
 */
public interface MessageDispatch
	extends MessageOutputStream
{
	// TODO: convert to fluent interface
	/**
	 * Gets a {@link FutureMessage} that will be fulfilled when the dispatch receives a message.
	 * <p>
	 * Use {@link FutureMessage#filter} to specify the exact message to receive.
	 * @return a promise of a future message.
	 */
	FutureMessage getFutureMessage();
	/**
	 * Gets a {@link FutureMessage} matching {@code filter} that will be fulfilled when the dispatch receives a message.
	 * @param filter the filter that will select the message to return.
	 * @return a promise of a future message.
	 */
	// TODO: Improve the filter application interface
	FutureMessage getFutureMessage(final MessageFilter filter);
}
