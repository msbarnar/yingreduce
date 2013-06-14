package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;


/**
 *
 */
public abstract class MessageFilterNode
{
	/*
	 * Doubly linked list
	 */
	protected MessageFilterNode lhs;
	protected MessageFilterNode rhs;

	protected MessageFilterNode(final MessageFilterNode lhs) {
		this.lhs = lhs;
	}
	protected void setRhs(final MessageFilterNode rhs) {
		this.rhs = rhs;
	}

	protected final MessageFilterNode getHead() {
		if (this.lhs == null) {
			return this;
		} else {
			return this.lhs.getHead();
		}
	}

	/*
	 * Filtering
	 */
	public final boolean filter(final Message message) {
		return this.getHead().filterDown(message, false);
	}
	protected abstract boolean filterDown(final Message message, final boolean lhs);
}
