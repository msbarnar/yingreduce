package edu.asu.ying.mapreduce.common.events;


import edu.asu.ying.mapreduce.common.filter.Filter;

import java.util.*;


/**
 * When fired, {@code FilteredValueEvent} only triggers event handlers whose filters match the value fired.
 */
public final class FilteredValueEvent<TValue>
{
	private final List<Map.Entry<Filter, EventHandler<TValue>>> handlers = new ArrayList<>();

	public final void attach(final Filter filter, final EventHandler<TValue> handler) {
		synchronized (this.handlers) {
			this.handlers.add(new AbstractMap.SimpleEntry<>(filter, handler));
		}
	}
	public final boolean detach(final Filter filter, final EventHandler<TValue> handler) {
		synchronized (this.handlers) {
			return this.handlers.remove(new AbstractMap.SimpleEntry<>(filter, handler));
		}
	}

	public final void fire(final Object sender, final TValue value) {
		synchronized (this.handlers) {
			final Iterator<Map.Entry<Filter, EventHandler<TValue>>> iter = this.handlers.iterator();
			while (iter.hasNext()) {
				final Map.Entry<Filter, EventHandler<TValue>> handler = iter.next();
				if (handler.getKey().match(value)) {
					// A return value of false means the handler is refusing further events
					if (!handler.getValue().onEvent(sender, value)) {
						iter.remove();
					}
				}
			}
		}
	}
}
