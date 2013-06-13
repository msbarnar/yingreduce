package edu.asu.ying.mapreduce.messaging;


/**
 * The root message filter; provides Any or All filters.
 */
public class MessageFilterRoot
{
	public final AbstractMessageFilter allOf = new MessageFilterAllOf(this);
	public final AbstractMessageFilter anyOf = new MessageFilterAnyOf(this);

	public MessageFilterRoot() {
	}

	public boolean match(final Message message) {
		return (this.allOf.match(message) && this.anyOf.match(message));
	}
}
