package edu.asu.ying.mapreduce.messaging.filter2;

/**
 * {@code on} provides fluent access to all of the filters.
 */
public final class on
{
	public static final <T> FilterOnType type(final Class<T> type) {
		return new FilterOnType(type);
	}

	public static final FilterNode allOf(final FilterNode ... filters) {
		return new FilterAllOf(filters);
	}

	public static final FilterNode anyOf(final FilterNode ... filters) {
		return new FilterAnyOf(filters);
	}
}
