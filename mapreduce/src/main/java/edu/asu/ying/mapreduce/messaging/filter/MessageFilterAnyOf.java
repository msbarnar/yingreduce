package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.Map;


/**
 * Filters messages that match any one of the criteria applied to this filter.
 */
public class MessageFilterAnyOf
	extends MessageFilterBase
{
	public MessageFilterAnyOf() {
	}

	/**
	 * Returns true only if the message matches every filter applied.
	 * @param message the message to match.
	 */
	@Override
	public boolean match(final Message message) {
		if (this.isActive()) {
			// Finish fast on match
			if (matchClass(message)) { return true; }
			if (matchId(message)) { return true; }
			if (matchSourceUri(message)) { return true; }
			if (matchProps(message)) { return true; }

			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns true if any property matches any of the values given
	 */
	private boolean matchProps(final Message message) {
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

	private boolean matchSourceUri(final Message message) {
		final URI msgSourceUri = message.getSourceUri();
		for (final URI uri : this.bySourceUri) {
			if (msgSourceUri.equals(uri)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchId(final Message message) {
		final String msgId = message.getId();
		for (final String id : this.byId) {
			if (msgId.equals(id)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchClass(final Message message) {
		final Class<? extends Message> msgClass = message.getClass();
		for (final Class<? extends Message> clazz : this.byClass) {
			if (msgClass.equals(clazz)) {
				return true;
			}
		}
		return false;
	}
}
