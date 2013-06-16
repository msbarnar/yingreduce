package edu.asu.ying.mapreduce.common.events;


import edu.asu.ying.mapreduce.common.filter.Filter;

import java.util.*;


/**
 * When fired, {@code FilteredValueEvent} only triggers event handlers whose filters match the value fired.
 */
public final class FilteredValueEvent<V>
{
	private final List<Map.Entry<Filter, EventHandler<V>>> handlers = new ArrayList<>();

	public final void attach(final Filter filter, final EventHandler<V> handler) {
		this.handlers.add(new AbstractMap.SimpleEntry<>(filter, handler));
	}
	public final boolean detach(final Filter filter, final EventHandler<V> handler) {
		return this.handlers.remove(new AbstractMap.SimpleEntry<>(filter, handler));
	}

	public final void fire(final Object sender, final V value) {
		final Iterator<Map.Entry<Filter, EventHandler<V>>> iter = this.handlers.iterator();
		while (iter.hasNext()) {
			final Map.Entry<Filter, EventHandler<V>> handler = iter.next();
			if (handler.getKey().match(value)) {
				// A return value of false means the handler is refusing further events
				if (!handler.getValue().onEvent(sender, value)) {
					iter.remove();
				}
			}
		}
	}
}
