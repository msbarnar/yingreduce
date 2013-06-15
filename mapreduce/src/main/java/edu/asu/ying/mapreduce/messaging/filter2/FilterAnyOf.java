package edu.asu.ying.mapreduce.messaging.filter2;


/**
 * {@code FilterAnyOf} successfully matches a value if any one of its children matches it.
 */
public final class FilterAnyOf
	extends FilterNodeBase
{
	FilterAnyOf(final FilterNode... filters) {
		super(filters);
	}
	/**
	 * Returns true if any one of the children matched the value, else false.
	 */
	@Override
	public final <V> boolean match(final V value) {
		for (final FilterNode child : this.children) {
			if (child.match(value)) {
				return true;
			}
		}
		return false;
	}
}
