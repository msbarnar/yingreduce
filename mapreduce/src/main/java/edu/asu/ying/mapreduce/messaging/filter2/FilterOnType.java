package edu.asu.ying.mapreduce.messaging.filter2;

/**
 * Matches values that have the appropriate type
 */
public final class FilterOnType
	implements FilterNode
{
	private final Class<?> type;

	FilterOnType(final Class<?> type) {
		this.type = type;
	}

	@Override
	public final <V> boolean match(final V value) {
		return this.type.equals(value.getClass());
	}
}
