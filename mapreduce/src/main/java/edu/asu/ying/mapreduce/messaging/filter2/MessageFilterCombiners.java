package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public class MessageFilterCombiners
	extends MessageFilterNode
{
	public final MessageFilterCombiner and;
	public final MessageFilterCombiner or;
	public final MessageFilterCombiner not;

	public MessageFilterCombiners(final MessageFilter2 lhs) {
		super(lhs);
		this.and = new MessageFilterAnd(lhs);
		this.or = new MessageFilterOr(lhs);
		this.not = new MessageFilterNot(lhs);
	}

	@Override
	protected boolean filterDown(final Message message, final boolean lhs) {
		return false;
	}
}
