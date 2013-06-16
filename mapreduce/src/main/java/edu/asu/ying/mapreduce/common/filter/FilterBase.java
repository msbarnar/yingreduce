package edu.asu.ying.mapreduce.common.filter;

import com.google.common.collect.Lists;

import java.util.List;



public abstract class FilterBase
	implements Filter
{
	protected final List<Filter> children;

	protected FilterBase(final Filter... filters) {
		this.children = Lists.newArrayList(filters);
	}

	@SuppressWarnings("unchecked")
	protected <T> T dynamicCast(final Object value) {
		try {
			return (T) value;
		} catch (final ClassCastException e) {
			return null;
		}
	}
}
