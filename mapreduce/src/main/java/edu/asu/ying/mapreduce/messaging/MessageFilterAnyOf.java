package edu.asu.ying.mapreduce.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;


/**
 * Provides a fluent interface for filtering messages returned in a {@link FutureMessage}.
 * <p>
 * The Any filter matches at least one of each criteria.
 */
public final class MessageFilterAnyOf
	extends AbstractMessageFilter
{
	public MessageFilterAnyOf(final MessageFilterRoot root) {
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

	/**
	 * Returns true if any property matches any of the values given
	 */
	private final boolean matchProps(final Message message) {
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
					if (value == null) {
						return true;
					}
				} else {
					if (messageProp.equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private final boolean matchSourceUri(final Message message) {
		final URI msgSourceUri = message.getSourceUri();
		for (final URI uri : this.bySourceUri) {
			if (msgSourceUri.equals(uri)) {
				return true;
			}
		}
		return false;
	}

	private final boolean matchId(final Message message) {
		boolean match = true;

		final String msgId = message.getId();
		for (final String id : this.byId) {
			if (msgId.equals(id)) {
				return true;
			}
		}
		return false;
	}

	private final boolean matchClass(final Message message) {
		final Class<? extends Message> msgClass = message.getClass();
		for (final Class<? extends Message> clazz : this.byClass) {
			if (msgClass.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}
