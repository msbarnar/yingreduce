package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;

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
		public static final Filter id(final String id) {
			return new FilterMessage.FilterOnId(id);
		}
		public static final Filter property(final Serializable key, final Serializable value) {
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
		final Message message = this.<Message>dynamicCast(value);
		if (message == null) {
			return false;
		} else {
			return this.match(message);
		}
	}

	/*********************************************************
	 * Message Filters
	 */
	private static final class FilterOnId extends FilterMessage
	{
		private final String id;
		private FilterOnId(final String id) { this.id = id; }

		@Override protected boolean match(final Message message) {
			if (this.id == null) {
				return message.getId() == null;
			} else {
				return this.id.equals(message.getId());
			}
		}
	}

	private static final class FilterOnProperty extends FilterMessage
	{
		private final Serializable key, value;
		private FilterOnProperty(final Serializable key, final Serializable value) {
			this.key = key;
			this.value = value;
		}

		@Override protected boolean match(final Message message) {
			if (this.key == null) {
				return false;
			}
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