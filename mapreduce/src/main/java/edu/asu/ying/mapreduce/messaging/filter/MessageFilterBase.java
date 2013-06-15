package edu.asu.ying.mapreduce.messaging.filter;

import edu.asu.ying.mapreduce.messaging.Message;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;

import java.io.Serializable;
import java.util.*;


/**
 * Base class for message filters
 */
public abstract class MessageFilterBase
{
	// Specifies that this filter is used
	protected boolean isActive = false;

	public MessageFilterBase allOf;
	public MessageFilterBase anyOf;
	public MessageFilterBase noneOf;

	protected final List<Class<? extends Message>> byClass = new ArrayList<>();
	protected final List<String> byId = new ArrayList<>();
	protected final List<ResourceIdentifier> bySourceUri = new ArrayList<>();
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

	public MessageFilterBase() {
	}

	public void bind(final MessageFilter root) {
		this.allOf = root.allOf;
		this.anyOf = root.anyOf;
		this.noneOf = root.noneOf;
	}

	public MessageFilterBase type(final Class<? extends Message> clazz) {
		synchronized (this) {
			this.isActive = true;
			this.byClass.add(clazz);
			return this;
		}
	}

	public MessageFilterBase id(final String id) {
		synchronized (this) {
			this.isActive = true;
			this.byId.add(id);
			return this;
		}
	}

	public MessageFilterBase sourceUri(final ResourceIdentifier uri) {
		synchronized (this) {
			this.isActive = true;
			this.bySourceUri.add(uri);
			return this;
		}
	}

	public MessageFilterBase property(final Serializable key, final Serializable value) {
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
