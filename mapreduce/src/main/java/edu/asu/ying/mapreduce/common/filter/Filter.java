package edu.asu.ying.mapreduce.common.filter;

import com.google.common.base.Preconditions;


/**
 * A {@code Filter} is a node in the tree of filters.
 */
public interface Filter
{
	/**
	 * Maps the value to the node's children and reduces their results.
	 */
	<V> boolean match(final V value);

	/************************************************************
	 * Common filters
	 */
	public static final class on
	{
		public static <T> FilterOnEquals<T> doesEqual(final T value) {
			return new on.FilterOnEquals<>(value);
		}
		public static <T> FilterOnClass classIs(final Class<T> type) {
			return new on.FilterOnClass(type);
		}
		public static Filter allOf(final Filter... filters) {
			return new on.FilterAllOf(filters);
		}
		public static Filter anyOf(final Filter... filters) {
			return new on.FilterAnyOf(filters);
		}
		public static Filter noneOf(final Filter... filters) {
			return new on.FilterNoneOf(filters);
		}

		/**********************************************************************
		 * Filters
		 */
		/**
		 * {@code FilterOnClass} Matches values that have the appropriate class.
		 */
		private static final class FilterOnEquals<T> implements Filter
		{
			private final T value;
			FilterOnEquals(final T value) {
				this.value = value;
			}

			@Override public final <V> boolean match(final V value) {
				if (this.value == null) {
					return value == null;
				}
				return this.value.equals(value);
			}
		}
		/**
		 * {@code FilterOnClass} Matches values that have the appropriate class.
		 */
		private static final class FilterOnClass implements Filter
		{
			private final Class<?> clazz;
			FilterOnClass(final Class<?> clazz) {
				Preconditions.checkNotNull(clazz);
				this.clazz = clazz;
			}

			@Override public final <V> boolean match(final V value) {
				return this.clazz.equals(value.getClass());
			}
		}
		/**
		 * {@code FilterAllOf} successfully matches a value if all of its children match it.
		 */
		private static final class FilterAllOf extends FilterBase
		{
			FilterAllOf(final Filter... filters) { super(filters); }

			/** Returns false if any one of the children failed to match the value, else true. */
			@Override public final <V> boolean match(final V value) {
				for (final Filter child : this.children) {
					if (!child.match(value)) {
						return false;
					}
				}
				return true;
			}
		}
		/**
		 * {@code FilterAnyOf} successfully matches a value if any one of its children matches it.
		 */
		private static final class FilterAnyOf extends FilterBase
		{
			FilterAnyOf(final Filter... filters) { super(filters); }

			/** Returns true if any one of the children matched the value, else false. */
			@Override public final <V> boolean match(final V value) {
				for (final Filter child : this.children) {
					if (child.match(value)) {
						return true;
					}
				}
				return false;
			}
		}
		/**
		 * {@code FilterNoneOf} successfully matches a value if none of its children match it.
		 */
		private static final class FilterNoneOf extends FilterBase
		{
			FilterNoneOf(final Filter... filters) { super(filters); }

			/** Returns true if any one of the children matched the value, else false. */
			@Override public final <V> boolean match(final V value) {
				for (final Filter child : this.children) {
					if (child.match(value)) {
						return false;
					}
				}
				return true;
			}
		}
	}
}
