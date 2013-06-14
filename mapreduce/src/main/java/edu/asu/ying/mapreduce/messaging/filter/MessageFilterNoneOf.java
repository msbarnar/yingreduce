package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;

/**
 * Filters messages that match exactly zero of the criteria applied to this filter.
 * <p>
 * {@link MessageFilterNoneOf} is the binary opposite of {@link MessageFilterAnyOf}.
 */
public class MessageFilterNoneOf
	extends MessageFilterAnyOf
{
	public MessageFilterNoneOf() {
	}

	/**
	 * Returns true only if the message matches none of the filters applied.
	 * @param message the message to match.
	 */
	@Override
	public final boolean match(final Message message) {
		if (this.isActive()) {
			return !super.match(message);
		} else {
			return true;
		}
	}
}
