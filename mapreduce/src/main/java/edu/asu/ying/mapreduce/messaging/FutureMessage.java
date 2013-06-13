package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.SettableFuture;
import edu.asu.ying.mapreduce.messaging.filter.MessageFilterRoot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Wraps a {@link com.google.common.util.concurrent.ListenableFuture} in a fluently filterable interface.
 */
public final class FutureMessage
{
	public static final MessageFilterRoot filter = new MessageFilterRoot();

	private final SettableFuture<Message> future;

	public FutureMessage(final SettableFuture<Message> future) {
		this.future = future;
		this.filter.anyOf.id("hi").type(Message.class).property("foo", "bar").property("foo", "baz");
	}

	/**
	 * Sets the {@link SettableFuture}, but first filters the message. If the filter does not match on the message,
	 * the value is not set.
	 * @param message the value to set
	 * @return true if the value is set; false if not set because of failure to match, or if the future was already set.
	 */
	public final boolean set(final Message message) {
		if (this.filter.match(message)) {
			return this.future.set(message);
		} else {
			return false;
		}
	}

	public final SettableFuture<Message> getFuture() { return this.future; }

	// Convenience methods

	/**
	 * @see com.google.common.util.concurrent.SettableFuture#get()
	 */
	public final Message get() throws ExecutionException, InterruptedException {
		return this.future.get();
	}
	/**
	 * @see com.google.common.util.concurrent.SettableFuture#get(long, java.util.concurrent.TimeUnit)
	 */
	public final Message get(final long timeout, final TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {

		return this.future.get(timeout, unit);
	}
}
