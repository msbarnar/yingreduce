package edu.asu.ying.mapreduce.messaging;

import com.google.common.util.concurrent.ListenableFuture;


/**
 * A {@link MessageDispatch} is a {@link MessageOutputStream} that associates recipient objects with specific messages
 * so that they can be asynchronously notified when a message of interest arrives.
 */
public interface MessageDispatch
	extends MessageOutputStream
{
	// TODO: convert to fluent interface
	/**
	 * Gets a {@link ListenableFuture} that will be fulfilled when the dispatch receives a message with a specific ID.
	 * @param id the ID of the message to forward.
	 * @return a promise of a future message with the given ID.
	 */
	public ListenableFuture<Message> getFutureMessageById(final String id);
	/**
	 * Gets a {@link ListenableFuture} that will be fulfilled when the dispatch receives a message of a specific class.
	 * @param messageClass the class of the message to forward.
	 * @return a promise of a future message with the given classs.
	 */
	public ListenableFuture<Message> getFutureMessageByClass(final Class<? extends Message> messageClass);
}
