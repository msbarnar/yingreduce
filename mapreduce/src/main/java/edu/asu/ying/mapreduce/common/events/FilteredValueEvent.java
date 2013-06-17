package edu.asu.ying.mapreduce.common.events;

import edu.asu.ying.mapreduce.common.filter.Filter;

import javax.annotation.Nullable;



public interface FilteredValueEvent<TValue> {
	void attach(final Filter filter, final EventHandler<TValue> handler);
	boolean detach(final Filter filter, final EventHandler<TValue> handler);
	void fire(final Object sender, final @Nullable TValue value);
}
