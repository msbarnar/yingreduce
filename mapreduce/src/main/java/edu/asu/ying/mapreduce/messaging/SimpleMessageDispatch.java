package edu.asu.ying.mapreduce.messaging;

import edu.asu.ying.mapreduce.messaging.filter.AbstractMessageFilter;

import java.util.*;


/**
 * {@link SimpleMessageDispatch} is a {@link MessageOutputStream} that associates messages with specific recipient
 * objects and provides those messages via {@link com.google.common.util.concurrent.ListenableFuture} promises.
 */
public class SimpleMessageDispatch
	implements MessageDispatch
{
	// All future messages to fulfill
	private final List<FutureMessage> futures = new ArrayList<>();

	/**
	 * Gets a {@link FutureMessage} that will be fulfilled when the dispatch receives a message.
	 * <p>
	 * Use {@link FutureMessage#filter} to specify the exact message to receive.
	 * @return a promise of a future message.
	 */
	public final FutureMessage getFutureMessage() {
		// FIXME: race condition
		//  1) Thread 1: The user gets a FutureMessage, and it is added to the list
		//  2) Thread 2: writes a message to the dispatch; the FutureMessage is set
		//  3) Thread 1: The user sets the filters on the message
		// Fix for now by making the default filter match nothing; the race condition then produces false negatives
		// instead of false positives.

		// Don't add anything to the list while it's being iterated
		synchronized (this.futures) {
			final FutureMessage future = new FutureMessage();
			this.futures.add(future);
			return future;
		}
	}

	@Override
	public FutureMessage getFutureMessage(final AbstractMessageFilter filter) {
		// Solves the race condition in getFutureMessage() by specifying the filter before adding the message
		synchronized (this.futures) {
			final FutureMessage future = new FutureMessage();
			future.filter.set(filter);
			this.futures.add(future);
			return future;
		}
	}

	/**
	 * Writes a message to the dispatch, forwarding it to any objects waiting on that message.
	 * @param message the message to dispatch.
	 */
	@Override
	public void write(final Message message) {
		// Don't let anybody change the list while we're iterating
		synchronized (this.futures) {
			final Iterator<FutureMessage> iter = this.futures.iterator();
			while (iter.hasNext()) {
				final FutureMessage future = iter.next();
				// If a future matches the message, set it once and get rid of it so we don't set it again
				if (future.match(message)) {
					future.set(message);
					iter.remove();
				}
			}
		}
	}
}
