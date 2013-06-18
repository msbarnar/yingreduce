package edu.asu.ying.mapreduce.common;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public final class Properties
	implements Serializable
{
	public static final Properties Empty = new Properties(ImmutableMap.<Serializable, Serializable>of());

	private static final long SerialVersionUID = 1L;

	private final Map<Serializable, Serializable> properties;

	public Properties() {
		this.properties = new HashMap<Serializable, Serializable>();
	}
	public Properties(final Map<Serializable, Serializable> of) {
		this.properties = of;
	}

	public final Serializable get(final Serializable key) {
		Preconditions.checkNotNull(key);

		return this.properties.get(key);
	}
	public final Serializable put(final Serializable key, final Serializable value) {
		Preconditions.checkNotNull(key);
		Preconditions.checkNotNull(value);

		return this.properties.put(key, value);
	}

	/**
	 * Gets the value of a property cast to the given class.
	 * </p>
	 * If the cast fails, the returned value is null.
	 * @param key the key of the property to get.
	 * @param type the type to which the value will be cast.
	 * @param <T> the type returned.
	 * @return the value of the property as {@code type}, or null if it could not be cast.
	 */
	public final <T> T getDynamicCast(final String key, final Class<T> type) {
		Preconditions.checkNotNull(key);
		Preconditions.checkNotNull(type);

		try {
			return type.cast(this.get(key));
		} catch (final ClassCastException e) {
			// TODO: logging
			return null;
		}
	}

	/**
	 * Gets the value of the property as a {@link String}, returning an empty string if the value was null.
	 */
	public final String getNullAsEmpty(final String key) {
		Preconditions.checkNotNull(key);

		final Optional<Serializable> value = Optional.fromNullable(this.get(key));
		if (value.isPresent()) {
			return Strings.emptyToNull(String.valueOf(value.get()));
		} else {
			return "";
		}
	}
}
