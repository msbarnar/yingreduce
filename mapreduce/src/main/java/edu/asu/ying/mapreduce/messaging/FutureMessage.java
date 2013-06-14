package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.*;
import edu.asu.ying.mapreduce.messaging.filter.MessageFilter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Wraps a {@link com.google.common.util.concurrent.ListenableFuture} in a fluently filterable interface.
 */
public final class FutureMessage
{
	public final MessageFilter filter = new MessageFilter();

	private final SettableFuture<Message> future;
	private final ListeningExecutorService executor;

	public FutureMessage() {
		this.future = SettableFuture.create();
		this.executor = null;
	}
	public FutureMessage(final SettableFuture<Message> future) {
		this.future = future;
		this.executor = null;
	}
	public FutureMessage(final ListeningExecutorService executor) {
		this.future = SettableFuture.create();
		this.executor = executor;
	}


	public final void addCallback(final FutureCallback<Message> callback) {
		if (this.executor == null) {
			Futures.addCallback(this.future, callback);
		} else {
			Futures.addCallback(this.future, callback, this.executor);
		}
	}

	/**
	 * Returns true if {@code message} matches the filter.
	 */
	public final boolean match(final Message message) {
		return this.filter.match(message);
	}

	/**
	 * Sets the future value only if the message matches the filter.
	 * @returns true if the value was set, false if not due to mismatch or future already being set.
	 */
	public final boolean setIfMatch(final Message message) {
		if (this.match(message)) {
			return this.set(message);
		} else {
			return false;
		}
	}

	public final ListenableFuture<Message> getFuture() { return this.future; }

	/*
	 * Convenience methods
	 */

	/**
	 * @see SettableFuture#set(Object)
	 */
	public final boolean set(final Message message) {
		return this.future.set(message);
	}
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
