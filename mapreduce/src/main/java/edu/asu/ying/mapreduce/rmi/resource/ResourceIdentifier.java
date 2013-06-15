package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;


/**
 * Identifies an item on the network.
 */
public final class ResourceIdentifier
	implements Serializable
{
	private static final long SerialVersionUID = 1L;

	private static final String SEPARATOR = "\\";

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

	public ResourceIdentifier(final String identifier)
			throws IllegalArgumentException, URISyntaxException {

		Preconditions.checkNotNull(Strings.emptyToNull(identifier));

		this.identifier = identifier;
		this.parse();
	}
	public ResourceIdentifier(final String scheme, final String address)
			throws IllegalArgumentException, URISyntaxException {

		Preconditions.checkNotNull(Strings.emptyToNull(scheme));
		Preconditions.checkNotNull(Strings.emptyToNull(address));

		this.identifier = scheme.concat(SEPARATOR).concat(address);
		this.parse();
	}
	public ResourceIdentifier(final String scheme, final String host, final int port)
			throws IllegalArgumentException, URISyntaxException {

		Preconditions.checkNotNull(Strings.emptyToNull(scheme));
		Preconditions.checkNotNull(Strings.emptyToNull(host));

		if (port > 0) {
			this.identifier = String.format("%s%s%s:%d", scheme, SEPARATOR, host, port);
		} else {
			this.identifier = scheme.concat(SEPARATOR).concat(host);
		}
		this.parse();
	}
	public ResourceIdentifier(final String scheme, final String host, final int port, final String path)
			throws IllegalArgumentException, URISyntaxException {

		Preconditions.checkNotNull(Strings.emptyToNull(scheme));
		Preconditions.checkNotNull(Strings.emptyToNull(host));
		Preconditions.checkNotNull(Strings.emptyToNull(path));

		if (port > 0) {
			this.identifier = String.format("%s%s%s:%d%s%s", scheme, SEPARATOR, host, port, SEPARATOR, path);
		} else {
			this.identifier = scheme.concat(SEPARATOR).concat(host).concat(SEPARATOR).concat(path);
		}
		this.parse();
	}
	public ResourceIdentifier(final String scheme, final String host, final int port,
	                          final String path, final String name)
			throws IllegalArgumentException, URISyntaxException {

		Preconditions.checkNotNull(Strings.emptyToNull(scheme));
		Preconditions.checkNotNull(Strings.emptyToNull(host));
		Preconditions.checkNotNull(Strings.emptyToNull(path));
		Preconditions.checkNotNull(Strings.emptyToNull(name));

		if (port > 0) {
			this.identifier = String.format("%s%s%s:%d%s%s%s%s", scheme, SEPARATOR, host, port, SEPARATOR, path,
			                                SEPARATOR, name);
		} else {
			this.identifier = scheme.concat(SEPARATOR).concat(host).concat(SEPARATOR).concat(path).concat(SEPARATOR)
			                        .concat(name);
		}
		this.parse();
	}

	private final void parse() throws URISyntaxException {
		this.parts = Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().split(this.identifier));
		if (Strings.isNullOrEmpty(this.getNullable(Part.Scheme))) {
			throw new URISyntaxException(this.identifier, "Scheme cannot be empty");
		}
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

	@Override
	public final String toString() { return this.identifier; }

	@Override
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
		try {
			this.parse();
		} catch (final URISyntaxException e) {
			throw new IOException(e);
		}
	}
}
