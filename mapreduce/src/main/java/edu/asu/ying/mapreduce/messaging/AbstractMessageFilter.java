package edu.asu.ying.mapreduce.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.*;


/**
 * Base class for message filters
 */
public abstract class AbstractMessageFilter
{
	protected final AbstractMessageFilter allOf;
	protected final AbstractMessageFilter anyOf;

	protected final List<Class<? extends Message>> byClass = new ArrayList<>();
	protected final List<String> byId = new ArrayList<>();
	protected final List<URI> bySourceUri = new ArrayList<>();
	protected final Map<Serializable, List<Serializable>> byProperty = new HashMap<>();

	public AbstractMessageFilter(final MessageFilterRoot root) {
		this.allOf = root.allOf;
		this.anyOf = root.anyOf;
	}

	public final AbstractMessageFilter type(final Class<? extends Message> clazz) {
		this.byClass.add(clazz);
		return this;
	}

	public final AbstractMessageFilter id(final String id) {
		this.byId.add(id);
		return this;
	}

	public final AbstractMessageFilter sourceUi(final URI uri) {
		this.bySourceUri.add(uri);
		return this;
	}

	public final AbstractMessageFilter property(final Serializable key, final Serializable value) {
		List<Serializable> vals = this.byProperty.get(key);
		if (vals == null) {
			vals = new ArrayList<>();
			this.byProperty.put(key, vals);
		}
		vals.add(value);
		return this;
	}

	public abstract boolean match(final Message message);
}
