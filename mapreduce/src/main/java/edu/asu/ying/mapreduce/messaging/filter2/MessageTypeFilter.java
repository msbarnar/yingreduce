package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public final class MessageTypeFilter
		extends MessageFilterMatcher
{
	private final Class<? extends Message> type;

	public MessageTypeFilter(final Class<? extends Message> type) {
		this.type = type;
	}

	@Override
	public boolean match(final Message message) {
		if (message == null || this.type == null) {
			return false;
		}
		return this.type.equals(message.getClass());
	}
}
