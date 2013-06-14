package edu.asu.ying.mapreduce.messaging.filter2;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;


/**
 *
 */
public final class MessagePropertyFilter
		extends MessageFilterMatcher
{
	private final Serializable key;
	private final Serializable value;

	public MessagePropertyFilter(final Serializable key, final Serializable value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean match(final Message message) {
		if (message == null || this.key == null) {
			return false;
		}

		final Serializable messageVal = message.getProperties().get(this.key);
		if (this.value == null) {
			return (messageVal == null);
		} else {
			return this.value.equals(messageVal);
		}
	}
}
