package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;
import java.net.URI;
import java.util.*;


/**
 *
 */
public class MessageFilterAllOf
	extends AbstractMessageFilter
{
	public MessageFilterAllOf(final MessageFilterRoot root) {
		super(root);
	}

	/**
	 * Returns true only if the message matches every filter applied.
	 * @param message the message to match.
	 */
	public final boolean match(final Message message) {
		// Fail fast on mismatch
		if (!matchClass(message)) { return false; }
		if (!matchId(message)) { return false; }
		if (!matchSourceUri(message)) { return false; }
		if (!matchProps(message)) { return false; }

		return true;
	}

	private final boolean matchProps(final Message message) {
		boolean match = true;

		// Each property can have multiple values, but this doesn't make any sense for a FilterAll.
		for (final Map.Entry<Serializable, List<Serializable>> prop : this.byProperty.entrySet()) {
			final List<Serializable> propVals = prop.getValue();
			if (propVals == null || propVals.size()==0) {
				continue;
			}

			final Serializable messageProp = message.getProperties().get(prop.getKey());
			for (final Serializable value : propVals) {
				// Allow null == null
				if (messageProp == null) {
					if (value != null) {
						match = false;
						break;
					}
				} else {
					if (!messageProp.equals(value)) {
						match = false;
						break;
					}
				}
			}
		}
		return match;
	}

	private final boolean matchSourceUri(final Message message) {
		boolean match = true;

		final URI msgSourceUri = message.getSourceUri();
		for (final URI uri : this.bySourceUri) {
			if (!msgSourceUri.equals(uri)) {
				match = false;
				break;
			}
		}
		return match;
	}

	private final boolean matchId(final Message message) {
		boolean match = true;

		final String msgId = message.getId();
		for (final String id : this.byId) {
			if (!msgId.equals(id)) {
				match = false;
				break;
			}
		}
		return match;
	}

	private final boolean matchClass(final Message message) {
		boolean match = true;

		final Class<? extends Message> msgClass = message.getClass();
		for (final Class<? extends Message> clazz : this.byClass) {
			if (!msgClass.equals(clazz)) {
				match = false;
				break;
			}
		}
		return match;
	}
}
