package edu.asu.ying.mapreduce.common;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.HashMap;


/**
 *
 */
public final class Properties
	extends HashMap<Serializable, Serializable>
{
	private static final long SerialVersionUID = 1L;

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
		try {
			return type.cast(this.get(key));
		} catch (final ClassCastException e) {
			// TODO: logging
			return null;
		}
	}

	/**
	 * Gets the value of the property as a {@link String}, returning null if the value is null or empty.
	 */
	public final String getEmptyAsNull(final String key) {
		final Optional<Serializable> value = Optional.fromNullable(this.get(key));
		if (value.isPresent()) {
			return Strings.emptyToNull(String.valueOf(value));
		} else {
			return null;
		}
	}
}
