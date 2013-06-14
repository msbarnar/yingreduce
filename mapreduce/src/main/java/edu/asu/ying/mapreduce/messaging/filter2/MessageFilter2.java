package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;


/**
 *
 */
public class MessageFilter2
	extends MessageFilterNode
{
	protected final MessageFilterMatcher filter;

	private static final MessageFilterCombiners startFilter(final MessageFilterMatcher filter) {
		return new MessageFilterCombiners(new MessageFilter2(null, filter));
	}

	public static final MessageFilterCombiner not = new MessageFilterNot(null);

	public static final MessageFilterCombiners id(final String id) {
		return startFilter(new MessageIdFilter(id));
	}
	public static final MessageFilterCombiners type(final Class<? extends Message> type) {
		return startFilter(new MessageTypeFilter(type));
	}
	public static final MessageFilterCombiners property(final Serializable key, final Serializable value) {
		return startFilter(new MessagePropertyFilter(key, value));
	}

	// Flanked by MessageFilterCombiners
	public MessageFilter2(final MessageFilterCombiner lhs, final MessageFilterMatcher filter) {
		super(lhs);
		this.filter = filter;
	}
	protected final void setRhs(final MessageFilterCombiner rhs) { this.rhs = rhs; }

	@Override
	protected boolean filterDown(final Message message, final boolean lhs) {
		if (this.rhs == null) {
			return this.filter.match(message);
		} else {
			return ((MessageFilterCombiner) this.rhs).filterDown(message, this.filter.match(message));
		}
	}
}
