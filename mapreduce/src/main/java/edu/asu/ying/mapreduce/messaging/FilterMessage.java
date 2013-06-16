package edu.asu.ying.mapreduce.messaging;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterBase;

import java.io.Serializable;


/**
 * {@code FilterMessage} provides common filters for {@link Message} types.
 */
public abstract class FilterMessage
	extends FilterBase
{
	/**
	 * Interface to message filters.
	 */
	public static class on
	{
		public static Filter id(final String id) {
			return new FilterMessage.FilterOnId(id);
		}
		public static Filter property(final Serializable key, final Serializable value) {
			return new FilterMessage.FilterOnProperty(key, value);
		}
	}

	/**
	 * The filtering method for message filters.
	 */
	protected abstract boolean match(final Message message);
	/**
	 * Convenience function for message filters.
	 */
	@Override
	public <V> boolean match(final V value) {
		if (!(value instanceof Message)) {
			return false;
		} else {
			return this.match((Message) value);
		}
	}

	/*********************************************************
	 * Message Filters
	 */
	private static final class FilterOnId extends FilterMessage
	{
		private final String id;
		private FilterOnId(final String id) {
			Preconditions.checkNotNull(Strings.emptyToNull(id));
			this.id = id;
		}

		@Override protected boolean match(final Message message) {
			if (message == null) return false;

			return this.id.equals(message.getId());
		}
	}

	private static final class FilterOnProperty extends FilterMessage
	{
		private final Serializable key, value;
		private FilterOnProperty(final Serializable key, final Serializable value) {
			Preconditions.checkNotNull(key);

			this.key = key;
			this.value = value;
		}

		@Override protected boolean match(final Message message) {
			if (message == null) return false;

			final Serializable messageValue = message.getProperties().get(this.key);
			if (this.value == null) {
				return messageValue == null;
			} else {
				return this.value.equals(messageValue);
			}
		}
	}
	/*********************************************************/
}