package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public class MessageFilterNot
	extends MessageFilterCombiner
{
	public MessageFilterNot(final MessageFilter2 lhs) {
		super(lhs);
	}

	@Override
	public boolean filterDown(final Message message, final boolean lhs) {
		if (this.rhs == null) {
			return false;
		} else {
			if (this.lhs == null) {
				return !this.rhs.filterDown(message, true);
			} else {
				return lhs && !this.rhs.filterDown(message, true);
			}
		}
	}
}
