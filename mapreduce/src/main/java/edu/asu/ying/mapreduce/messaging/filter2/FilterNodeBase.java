package edu.asu.ying.mapreduce.messaging.filter2;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;



public abstract class FilterNodeBase
	implements FilterNode
{
	protected final List<FilterNode> children;

	protected FilterNodeBase(final FilterNode ... filters) {
		this.children = Lists.newArrayList(filters);
	}

	protected <T> T dynamicCast(final Object value) {
		try {
			return (T) value;
		} catch (final ClassCastException e) {
			return null;
		}
	}
}
