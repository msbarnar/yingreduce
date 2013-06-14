package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.Serializable;
import java.net.URI;
import java.util.*;


/**
 * Base class for message filters
 */
public abstract class AbstractMessageFilter
{
	// Specifies that this filter is used
	protected boolean isActive = false;

	public AbstractMessageFilter allOf;
	public AbstractMessageFilter anyOf;
	public AbstractMessageFilter noneOf;

	protected final List<Class<? extends Message>> byClass = new ArrayList<>();
	protected final List<String> byId = new ArrayList<>();
	protected final List<URI> bySourceUri = new ArrayList<>();
	protected final Map<Serializable, List<Serializable>> byProperty = new HashMap<>();

	public void clear() {
		synchronized (this) {
			this.isActive = false;
			this.byClass.clear();
			this.byId.clear();
			this.bySourceUri.clear();
			this.byProperty.clear();
		}
	}

	public AbstractMessageFilter() {
	}

	public final void bind(final MessageFilter root) {
		this.allOf = root.allOf;
		this.anyOf = root.anyOf;
		this.noneOf = root.noneOf;
	}

	public final AbstractMessageFilter type(final Class<? extends Message> clazz) {
		synchronized (this) {
			this.isActive = true;
			this.byClass.add(clazz);
			return this;
		}
	}

	public final AbstractMessageFilter id(final String id) {
		synchronized (this) {
			this.isActive = true;
			this.byId.add(id);
			return this;
		}
	}

	public final AbstractMessageFilter sourceUi(final URI uri) {
		synchronized (this) {
			this.isActive = true;
			this.bySourceUri.add(uri);
			return this;
		}
	}

	public final AbstractMessageFilter property(final Serializable key, final Serializable value) {
		synchronized (this) {
			this.isActive = true;
			List<Serializable> vals = this.byProperty.get(key);
			if (vals == null) {
				vals = new ArrayList<>();
				this.byProperty.put(key, vals);
			}
			vals.add(value);
			return this;
		}
	}

	public boolean isActive() { return this.isActive; }

	public abstract boolean match(final Message message);
}
