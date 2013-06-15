package edu.asu.ying.mapreduce.events;


import edu.asu.ying.mapreduce.messaging.filter2.Filter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Vector;


/**
 * When fired, {@code FilteredValueEvent} only triggers event handlers whose filters match the value fired.
 */
public final class FilteredValueEvent<V>
{
	private final Vector<Map.Entry<Filter, EventHandler<V>>> handlers = new Vector<>();

	public final void attach(final Filter filter, final EventHandler<V> handler) {
		this.handlers.add(new AbstractMap.SimpleEntry<>(filter, handler));
	}
	public final boolean detach(final EventHandler<V> handler) {
		return this.handlers.remove(handler);
	}

	public final void fire(final Object sender, final V args) {
		for (final EventHandler<V> handler : this.handlers) {
			handler.onEvent(sender, args);
		}
	}
}
