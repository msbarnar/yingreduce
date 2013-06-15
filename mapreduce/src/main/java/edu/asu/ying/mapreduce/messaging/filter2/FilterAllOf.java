package edu.asu.ying.mapreduce.messaging.filter2;


import com.google.common.collect.Lists;


/**
 * {@code FilterAllOf} successfully matches a value if all of its children match it.
 */
public final class FilterAllOf
	extends FilterNodeBase
{
	FilterAllOf(final FilterNode ... filters) {
		super(filters);
	}
	/**
	 * Returns false if any one of the children failed to match the value, else true.
	 */
	@Override
	public final <V> boolean match(final V value) {
		for (final FilterNode child : this.children) {
			if (!child.match(value)) {
				return false;
			}
		}
		return true;
	}
}
