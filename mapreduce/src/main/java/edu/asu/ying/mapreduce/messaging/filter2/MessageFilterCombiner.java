package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;


/**
 *
 */
public abstract class MessageFilterCombiner
	extends MessageFilterNode
{
	protected MessageFilterCombiner(final MessageFilter2 lhs) {
		super(lhs);
	}

	private final MessageFilterCombiners activate(final MessageFilterMatcher filter) {
		if (this.lhs != null) {
			this.lhs.setRhs(this);
		}
		this.rhs = new MessageFilter2(this, filter);
		return new MessageFilterCombiners((MessageFilter2) this.rhs);
	}

	public final MessageFilterCombiners id(final String id) {
		return this.activate(new MessageIdFilter(id));
	}
	public final MessageFilterCombiners type(final Class<? extends Message> type) {
		return this.activate(new MessageTypeFilter(type));
	}
	public final MessageFilterCombiners property(final Serializable key, final Serializable value) {
		return this.activate(new MessagePropertyFilter(key, value));
	}

	protected abstract boolean filterDown(final Message message, final boolean lhs);
}
