package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


/**
 * Identifies an item on the network.
 */
public final class ResourceIdentifier
	implements Serializable
{
	private static final long SerialVersionUID = 1L;

	private enum Part {
		Scheme,
		Address,
		Path,
		Name
	}

	private final String identifier;
	// Not serialized; reparse on deserialization
	private transient List<String> parts;
	private transient String host;
	private transient int port;

	public ResourceIdentifier(final String identifier) {
		this.identifier = identifier;
		this.parse();
	}

	private final void parse() {
		this.parts = Lists.newArrayList(Splitter.on('/').trimResults().split(identifier));
		final String address = this.getNullable(Part.Address);

		this.host = address;
		this.port = -1;

		if (address != null) {
			final List<String> hostParts
					= Lists.newArrayList(Splitter.on(':').trimResults().split(address));
			if (hostParts.size() > 1) {
				try {
					this.port = Integer.parseInt(hostParts.get(hostParts.size()-1));
					this.host = hostParts.get(0);
				} catch (final NumberFormatException e) {
				}
			}
		}
	}

	private final String getNullable(final Part part) {
		try {
			return this.parts.get(part.ordinal());
		} catch (final IndexOutOfBoundsException e) {
			return null;
		}
	}

	public final String getScheme() { return getNullable(Part.Scheme); }
	public final String getAddress() { return getNullable(Part.Address); }
	public final String getHost() { return this.host; }
	public final int getPort() { return this.port; }
	public final String getPath() { return getNullable(Part.Path); }
	public final String getName() { return getNullable(Part.Name); }

	public final String toString() { return this.identifier; }

	public final boolean equals(final Object rhs) {
		if (rhs == null) {
			return false;
		}
		if (rhs.toString() == null) {
			return this.identifier == null;
		}
		return rhs.toString().equals(this.identifier);
	}

	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		// Reparse the identifier string
		this.parse();
	}
}
