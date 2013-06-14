package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public final class MessageIdFilter
		extends MessageFilterMatcher
{
	private final String id;

	public MessageIdFilter(final String id) {
		this.id = id;
	}

	@Override
	public boolean match(final Message message) {
		if (message == null || this.id == null) {
			return false;
		}
		return this.id.equals(message.getId());
	}
}
