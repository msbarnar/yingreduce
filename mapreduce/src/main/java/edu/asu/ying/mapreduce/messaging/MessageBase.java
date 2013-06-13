package edu.asu.ying.mapreduce.messaging;

import com.google.common.base.Optional;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Base class for a basic {@link Message}.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     <li>{@code id} - the universally unique identifier of this message.</li>
 *     <li>{@code destination-uri} - the URI of the destination host of this message, used by the network layer for
 *     routing.</li>
 *     <li>{@code replication} - the maximum number of hosts matching the destination URI to which this message will be
 *     send.</li>
 * </ul>
 */
public abstract class MessageBase
	implements Message
{
	protected final Map<Serializable, Serializable> properties = new HashMap<Serializable, Serializable>();

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
	public MessageBase(final URI destinationUri) {
		this.setDestinationUri(destinationUri);
	}
	public MessageBase(final String id, final URI destinationUri) {
		this.setDestinationUri(destinationUri);
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
	public Map<Serializable, Serializable> getProperties() { return this.properties; }

	public void setSourceUri(final URI uri) { this.properties.put("source-uri", uri); }
	@Override
	public URI getSourceUri() {
		try {
			return (URI) this.properties.get("source-uri");
		} catch (final ClassCastException e) {
			// TODO: Logging
			e.printStackTrace();
			return null;
		}
	}

	public void setDestinationUri(final URI uri) { this.properties.put("destination-uri", uri); }
	@Override
	public URI getDestinationUri() {
		try {
			return (URI) this.properties.get("destination-uri");
		} catch (final ClassCastException e) {
			// TODO: Logging
			e.printStackTrace();
			return null;
		}
	}

	public void setReplication(final int replication) { this.properties.put("replication", replication); }
	/**
	 * Replication is the maximum number of hosts matching the destination URI to which this message will be delivered.
	 * @return a number equal to or greater than 1 (default).
	 */
	@Override
	public int getReplication() {
		final Optional<Serializable> replication = Optional.fromNullable(this.properties.get("replication"));
		if (!replication.isPresent()) {
			this.setReplication(1);
		} else {
			try {
				return (Integer) replication.get();
			} catch (final ClassCastException e) {
			}
		}
		// Default case
		this.setReplication(1);
		return 1;
	}
}