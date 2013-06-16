package edu.asu.ying.mapreduce.common.concurrency;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import edu.asu.ying.mapreduce.common.events.EventHandler;
import edu.asu.ying.mapreduce.common.events.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.Filter;

import java.util.*;


/**
 * {@code FilteredFutures} provides future values from a {@link FilteredValueEvent} that match a given {@link Filter}.
 */
public class FilteredFutures<V>
	implements EventHandler<V>
{
	/**
	 * Returns a {@code FilteredFutures} instance that receives values from the given {@link FilteredValueEvent}.
	 * @param event the event that provides values to filter.
	 * @param <V> the type of value to receive
	 * @return a {@code FilteredFutures} instance attached to the given event.
	 */
	public static <V> FilteredFutures<V> getFutureValuesFrom(final FilteredValueEvent<V> event) {
		return new FilteredFutures<>(event);
	}

	/**
	 * Cancels any unfulfilled futures in a list.
	 * @param remainingFutures the list of futures in which all unfulfilled items will be cancelled.
	 */
	public static <V> void cancelRemaining(final List<ListenableFuture<V>> remainingFutures,
	                                       final boolean mayInterruptIfRunning) {
		for (final ListenableFuture<V> future : remainingFutures) {
			if (!future.isCancelled() && !future.isDone()) {
				future.cancel(mayInterruptIfRunning);
			}
		}
	}

	// Register on this event to receive values from it
	private final FilteredValueEvent<V> event;
	// These are the futures we returned.
	// Every time we receive a value, pop one of these off the stack and set it.
	private Deque<SettableFuture<V>> unfulfilledFutures;

	private FilteredFutures(final FilteredValueEvent<V> event) {
		this.event = event;
	}

	/**
	 * Initializes {@code count} futures, but does not begin listening.
	 * </p>
	 * Supply a filter with {@link FilteredFutures#filter} to begin receiving values.
	 * @param count the number of future values to receive.
	 */
	public final FilteredFutures<V> get(final int count) {
		this.unfulfilledFutures = new ArrayDeque<>(count);
		for (int i = 0; i < count; i++) {
			this.unfulfilledFutures.push(SettableFuture.<V>create());
		}
		// We don't have a filter yet, so don't start listening for values
		return this;
	}

	/**
	 * Applies the given filter and begins listening for values that match it.
	 * @param on the filter on which values will be matched.
	 * @return a list of {@link ListenableFuture} that will be fulfilled when values matching the filter arrive.
	 */
	public final List<ListenableFuture<V>> filter(final Filter on) {
		// Default to returning one future
		if (this.unfulfilledFutures == null) {
			this.get(1);
		}
		// Now that we have a filter we can start listening
		this.event.attach(on, this);
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
	public boolean onEvent(final Object sender, final V value) {
		// Clear all cancelled futures
		while ((this.unfulfilledFutures.peek() != null) && this.unfulfilledFutures.peek().isCancelled()) {
			this.unfulfilledFutures.pop();
		}
		// Fulfill futures until there are none left to fulfill
		if (this.unfulfilledFutures.peek() != null) {
			this.unfulfilledFutures.pop().set(value);
			// Return true if we have more futures, false if not
			return (this.unfulfilledFutures.peek() != null);
		} else {
			// No more futures to set; return false to signal refusal of further events
			return false;
		}
	}
}
