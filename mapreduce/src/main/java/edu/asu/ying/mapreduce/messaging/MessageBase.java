package edu.asu.ying.mapreduce.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


/**
 * Base class for a basic {@link Message}.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     <li>{@code destination-uri}: the URI of the destination host of this message, used by the network layer for
 *     routing.</li>
 * </ul>
 */
public abstract class MessageBase
	implements Message
{
	private final Map<Serializable, Serializable> properties = new HashMap<Serializable, Serializable>();

	/*
	 * Constructors
	 */
	public MessageBase() {
	}
	public MessageBase(final URI destinationUri) {
		this.setDestinationUri(destinationUri);
	}

	/*
	 * Accessors
	 */
	@Override
	public final Map<Serializable, Serializable> getProperties() { return this.properties; }

	protected final void setDestinationUri(final URI uri) { this.properties.put("destination-uri", uri); }
	@Override
	public final URI getDestinationUri() {
		try {
			return (URI) this.properties.get("destination-uri");
		} catch (final ClassCastException e) {
			// TODO: Logging
			e.printStackTrace();
			return null;
		}
	}
}
