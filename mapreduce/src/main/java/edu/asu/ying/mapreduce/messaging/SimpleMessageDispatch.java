package edu.asu.ying.mapreduce.messaging;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;


/**
 * {@link SimpleMessageDispatch} is a {@link MessageOutputStream} that associates messages with specific recipient
 * objects and provides those messages via {@link com.google.common.util.concurrent.ListenableFuture} promises.
 */
public class SimpleMessageDispatch
	implements MessageDispatch
{
	// Lookup table for message ID -> futures to fulfill
	private final Map<String, List<ListenableFuture<Message>>> idFutureMap = new HashMap<>();
	// Lookup table for message class -> futures
	private final Map<Class<?>, List<ListenableFuture<Message>>> classFutureMap = new HashMap<>();

	/**
	 * Registers a {@link ListenableFuture} to be fulfilled by a message with a specific ID.
	 * @param messageId the ID of the message to wait for.
	 * @return a {@link ListenableFuture} promise of a message with the given ID.
	 */
	public final ListenableFuture<Message> getFutureMessageById(final String messageId) {
		final ListenableFuture<Message> future = SettableFuture.create();
		// Add the future to the lookup table
		List<ListenableFuture<Message>> futures = this.idFutureMap.get(messageId);
		if (futures == null) {
			futures = new ArrayList<>();
			this.idFutureMap.put(messageId, futures);
		}
		futures.add(future);

		return future;
	}

	/**
	 * Registers a {@link ListenableFuture} to be fulfilled by a message of a specific class.
	 * @param messageClass the class of the message to wait for.
	 * @return a {@link ListenableFuture} promise of a message of the given class.
	 */
	public final ListenableFuture<Message> getFutureMessageById(final Class<Message> messageClass) {
		final ListenableFuture<Message> future = SettableFuture.create();
		// Add the future to the lookup table
		List<ListenableFuture<Message>> futures = this.classFutureMap.get(messageClass);
		if (futures == null) {
			futures = new ArrayList<>();
			this.classFutureMap.put(messageClass, futures);
		}
		futures.add(future);

		return future;
	}

	/**
	 * Writes a message to the dispatch, forwarding it to any objects waiting on that message.
	 * @param message the message to dispatch.
	 */
	@Override
	public void write(final Message message) {
		// Accumulate all of the futures until the end
		final Stack<SettableFuture<Message>> futures = new Stack<>();

		Optional<List<ListenableFuture<Message>>> matches;
		// Match on class
		matches = Optional.fromNullable(this.classFutureMap.get(message.getClass()));
		if (matches.isPresent()) {
			// Fulfill the futures and remove them from the map
			for (final ListenableFuture<Message> future : matches.get()) {
				((SettableFuture<Message>) future).set(message);
			}
			this.classFutureMap.remove(message.getClass());
		}

		// Match on message ID
		matches = Optional.fromNullable(this.idFutureMap.get(message.getId()));
		if (matches.isPresent()) {
			// Fulfill the futures and remove them from the map
			for (final ListenableFuture<Message> future : matches.get()) {
				((SettableFuture<Message>) future).set(message);
			}
			this.idFutureMap.remove(message.getClass());
		}
	}
}
