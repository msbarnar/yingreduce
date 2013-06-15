package edu.asu.ying.mapreduce.concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.asu.ying.mapreduce.events.EventHandler;
import edu.asu.ying.mapreduce.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.messaging.filter2.Filter;

import java.util.*;


/**
 * {@code FilteredFutures}
 */
public class FilteredFutures<V>
	implements EventHandler<V>
{
	public static <V> FilteredFutures<V> create(final FilteredValueEvent<V> event) {
		return new FilteredFutures<>(event);
	}

	// Register on this event to receive values from it
	private final FilteredValueEvent<V> event;
	// These are the futures we returned.
	// Every time we receive a value, pop one of these off the stack and set it.
	private Deque<SettableFuture<V>> unfulfilledFutures;
	// This is the filter that will match the values we get
	private Filter filter;

	private FilteredFutures(final FilteredValueEvent<V> event) {
		this.event = event;
	}

	public final FilteredFutures<V> get(final int count) {
		this.unfulfilledFutures = new ArrayDeque<>(count);
		for (int i = 0; i < count; i++) {
			this.unfulfilledFutures.push(SettableFuture.<V>create());
		}
		// We don't have a filter yet, so don't start listening for values
		return this;
	}

	public final List<ListenableFuture<V>> filter(final Filter on) {
		this.filter = on;
		// Now that we have a filter we can start listening
		this.event.attach(this.filter, this);
		return new ArrayList<ListenableFuture<V>>(this.unfulfilledFutures);
	}

	/**
	 * Fulfills one pending {@link ListenableFuture}.
	 * </p>
	 * If there are no more futures to fulfill, the event handler detaches itself from the event.
	 * @param sender the object that fired the event.
	 * @param value the value to set on the future.
	 */
	@Override
	public void onEvent(final Object sender, final V value) {
		// Fulfill futures until there are none left to fulfill
		if (this.unfulfilledFutures.peek() != null) {
			this.unfulfilledFutures.pop().set(value);
		} else {
			// No more futures to set, so stop listening
			this.event.detach(this);
		}
	}
}
