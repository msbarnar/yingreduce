package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.ListenableFuture;
import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;

import java.util.List;


/**
 * {@code FutureMessages} returns messages when they arrive at some point in the future.
 * </p>
 * When called, it returns a list of {@link com.google.common.util.concurrent.ListenableFuture}, one for each message
 * that's desired.</br>
 * The futures will be fulfilled in order of their return as messages arrive. When the last future is
 * fulfilled, the {@code FutureMessages} instance goes out of scope.
 * </p>
 * In the background, a call to {@code FutureMessages} queues the returned futures and sets up a
 * {@link edu.asu.ying.mapreduce.events.FilteredMessageCallback} on the incoming message handler.</br>
 * As the message handler receives messages, it checks them against every registered {@code FilteredCallback} and
 * passes them to any callbacks that match.
 */
public class FutureMessages
{
	/**
	 * Returns a {@link List} of {@link ListenableFuture}, each of which will be fulfilled on one or fewer
	 * {@link Message} objects that satisfy the {@link MessageFilter} given in {@code on}.
	 */
	public final List<ListenableFuture<? extends Message>> filter(final MessageFilter on) {
		return null;
	}
}
