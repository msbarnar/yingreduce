package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public class MessageFilterAnd
	extends MessageFilterCombiner
{
	public MessageFilterAnd(final MessageFilter2 lhs) {
		super(lhs);
	}

	@Override
	public boolean filterDown(final Message message, final boolean lhs) {
		if (this.rhs == null) {
			return false;
		} else {
			return lhs && this.rhs.filterDown(message, lhs);
		}
	}
}
