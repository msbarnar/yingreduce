package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 * {@code MessageFilter} provides common filters for {@link Message} types.
 */
public abstract class MessageFilter2
	extends FilterNodeBase
{
	/**
	 * Interface to message filters.
	 */
	public static class on {
		public static final FilterNode id(final String id) {
			return new MessageIdFilter(id);
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
	private static final class MessageIdFilter extends MessageFilter2
	{
		private final String id;
		private MessageIdFilter(final String id) { this.id = id; }

		@Override protected boolean match(final Message message) {
			if (this.id == null) {
				return message.getId() == null;
			} else {
				return this.id.equals(message.getId());
			}
		}
	}
	/*********************************************************/
}