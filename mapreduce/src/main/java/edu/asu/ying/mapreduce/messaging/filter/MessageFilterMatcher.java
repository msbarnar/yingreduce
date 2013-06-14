package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public abstract class MessageFilterMatcher
{
	public abstract boolean match(final Message message);
}
