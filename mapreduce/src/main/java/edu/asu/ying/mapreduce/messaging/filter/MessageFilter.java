package edu.asu.ying.mapreduce.messaging.filter;


import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;
import java.net.URI;


/**
 * The root message filter; combines different {@link MessageFilterBase} into a single filter.
 */
public class MessageFilter
	extends MessageFilterBase
{
	public final MessageFilterBase allOf = new MessageFilterAllOf();
	public final MessageFilterBase anyOf = new MessageFilterAnyOf();
	public final MessageFilterBase noneOf = new MessageFilterNoneOf();

	private MessageFilterBase setFilter;

	private boolean matchAny = false;

	public MessageFilter() {
		this.allOf.bind(this);
		this.anyOf.bind(this);
		this.noneOf.bind(this);
	}

	public final void clear() {
		this.setFilter = null;
		this.allOf.clear();
		this.anyOf.clear();
		this.noneOf.clear();
		this.matchAny = false;
	}

	public final void any() {
		this.clear();
		this.matchAny = true;
	}

	public final void set(final MessageFilterBase filter) {
		this.setFilter = filter;
	}

	@Override
	public boolean match(final Message message) {
		if (this.setFilter != null) {
			return this.setFilter.match(message);
		}

		if (allOf.isActive() || anyOf.isActive() || noneOf.isActive()) {
			return this.anyOf.match(message) && this.noneOf.match(message) && this.allOf.match(message);
		} else {
			if (this.matchAny) {
				return true;
			} else {
				// Match nothing by default
				return false;
			}
		}
	}

	/**
	 * The default implementation for the MessageFilter is to require all of the filters.
	 * @param clazz the message class that must be matched.
	 */
	@Override
	public final MessageFilterBase type(final Class<? extends Message> clazz) {
		this.allOf.type(clazz);
		return this;
	}

	/**
	 * The default implementation for the MessageFilter is to require all of the filters.
	 * @param id the message ID that must be matched.
	 */
	@Override
	public final MessageFilterBase id(final String id) {
		this.allOf.id(id);
		return this;
	}

	/**
	 * The default implementation for the MessageFilter is to require all of the filters.
	 * @param uri the message source URI that must be matched.
	 */
	@Override
	public final MessageFilterBase sourceUri(final URI uri) {
		this.allOf.sourceUri(uri);
		return this;
	}

	/**
	 * The default implementation for the MessageFilter is to require all of the filters.
	 * @param key the key of the message property that will be matched.
	 * @param value the value of the property that must be matched.
	 */
	@Override
	public final MessageFilterBase property(final Serializable key, final Serializable value) {
		this.allOf.property(key, value);
		return this;
	}
}
