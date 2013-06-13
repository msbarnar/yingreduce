package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.ListenableFuture;


/**
 * Wraps a {@link com.google.common.util.concurrent.ListenableFuture} in a fluently filterable interface.
 */
public final class FutureMessage
{
	public static final MessageFilterRoot filter = new MessageFilterRoot();

	private final ListenableFuture<Message> future;

	public FutureMessage(final ListenableFuture<Message> future) {
		this.future = future;
		this.filter.anyOf.id("hi").type(Message.class).property("foo", "bar").property("foo", "baz");
	}
}
