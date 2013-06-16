package edu.asu.ying.mapreduce.messaging;

import com.google.common.base.Optional;
import edu.asu.ying.mapreduce.Properties;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Base class for a basic {@link Message}.
 * <p>
 * The following properties are defined on this message:
 * <ul>
 *     <li>{@code id} - the universally unique identifier of this message.</li>
 *     <li>{@code destination-uri} - the URI of the destination host of this message, used on the network layer for
 *     routing.</li>
 *     <li>{@code replication} - the maximum number of hosts matching the destination URI to which this message will be
 *     send.</li>
 * </ul>
 */
public abstract class MessageBase
	implements Message
{
	private static final long SerialVersionUID = 1L;

	protected final Properties properties = new Properties();

	/*
	 * Constructors
	 */

	/**
	 * Initializes the message with a random ID.
	 */
	public MessageBase() {
		this.setId();
	}
	public MessageBase(final String id) {
		this.setId(id);
	}
	public MessageBase(final ResourceIdentifier destinationUri) {
		this.setDestinationUri(destinationUri);
	}
	public MessageBase(final String id, final ResourceIdentifier destinationUri) {
		this.setDestinationUri(destinationUri);
	}

	public <T> T getNullableProperty(final String key, final Class<T> type) {
		try {
			return type.cast(this.properties.get(key));
		} catch (final ClassCastException e) {
			// TODO: logging
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * Accessors
	 */
	public void setId(final String id) { this.properties.put("id", id); }
	/**
	 * Initializes the message ID with a random {@link UUID}.
	 */
	public void setId() { this.setId(UUID.randomUUID().toString()); }
	public void setId(final UUID id) { this.setId(id.toString()); }

	@Override
	public String getId() {
		final Optional<Serializable> id = Optional.fromNullable(this.properties.get("id"));
		if (!id.isPresent()) {
			// We can't have no id; set a random one.
			this.setId();
			return this.getId();
		}

		return String.valueOf(id.get());
	}

	@Override
	public Properties getProperties() { return this.properties; }

	public void setSourceUri(final ResourceIdentifier uri) { this.properties.put("source-uri", uri); }
	@Override
	public ResourceIdentifier getSourceUri() {
		return this.getNullableProperty("source-uri", ResourceIdentifier.class);
	}

	public void setDestinationUri(final ResourceIdentifier uri) { this.properties.put("destination-uri", uri); }
	@Override
	public ResourceIdentifier getDestinationUri() {
		return this.getNullableProperty("destination-uri", ResourceIdentifier.class);
	}

	/**
	 * Replication is the maximum number of hosts matching the destination URI to which this message will be delivered.
	 * @return a number equal to or greater than 1 (default).
	 */
	@Override
	public int getReplication() { return this.getDestinationUri().getReplication(); }
}