package edu.asu.ying.mapreduce.net.resource;

import com.google.common.base.Preconditions;
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
 * </p>
 * Format:
 * </p>
 * {@code scheme\(replication)host:port\path\name}
 */
public final class ResourceIdentifier
	implements Serializable
{
	public static final ResourceIdentifier Empty = new ResourceIdentifier();

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
	private transient int port = -1;
	private transient int replication = 1;

	private ResourceIdentifier() {
		this.identifier = "";
	}

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

	private void parse() throws URISyntaxException {
		this.parts = Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().split(this.identifier));
		if (Strings.isNullOrEmpty(this.getPartOrNull(Part.Scheme))) {
			throw new URISyntaxException(this.identifier, "Scheme cannot be empty");
		}
		final String address = this.getPartOrNull(Part.Address);

		if (address != null) {
			final List<String> hostParts = Lists.newArrayList(Splitter.on(':').trimResults().split(address));
			// Parse replication
			final String firstPart = hostParts.get(0);
			this.host = firstPart;
			if (firstPart.charAt(0) == '(') {
				final int closeParen = firstPart.indexOf(')');
				if (closeParen > 0) {
					try {
						this.replication = Integer.parseInt(firstPart.substring(1, closeParen));
						// Set the host minus the replication
						this.host = firstPart.substring(closeParen+1);
					} catch (final NumberFormatException e) {
					}
				}
			}
			if (hostParts.size() > 1) {
				try {
					this.port = Integer.parseInt(hostParts.get(hostParts.size()-1));
				} catch (final NumberFormatException e) {
				}
			}

			// Make sure the address is host:port (get rid of replication)
			this.parts.set(Part.Address.ordinal(), this.host.concat(":").concat(String.valueOf(this.port)));
		}

		if (this.replication < 1) {
			this.replication = 1;
		}
	}

	/**
	 * Gets the string value of the specified part or, if the part does not have a value, the empty string.
	 */
	private String getPartOrNull(final Part part) {
		try {
			return this.parts.get(part.ordinal());
		} catch (final IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Gets the part of the identifier that specifies to which service this identifier is delegated.
	 * </p>
	 * E.g. an identifier with the scheme "resource" represents a {@link RemoteResource}.
	 */
	public final String getScheme()     { return getPartOrNull(Part.Scheme); }
	public final String getAddress()    { return getPartOrNull(Part.Address); }
	public final String getHost()       { return this.host; }
	public final int getPort()          { return this.port; }
	public final String getPath()       { return getPartOrNull(Part.Path); }
	public final String getName()       { return getPartOrNull(Part.Name); }
	public final int getReplication()   { return this.replication; }

	/**
	 * Deserializes the identifier string normally and then parses it into the identifier parts.
	 */
	private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		// Reparse the identifier string
		try {
			this.parse();
		} catch (final URISyntaxException e) {
			throw new IOException(e);
		}
	}

	@Override
	public final String toString() {
		return this.identifier;
	}

	@Override
	public final boolean equals(final Object rhs) {
		if (this == rhs) return true;
		if (!(rhs instanceof ResourceIdentifier)) return false;

		if (rhs.toString() == null) {
			return this.identifier == null;
		}
		return rhs.toString().equals(this.identifier);
	}

	@Override
	public final int hashCode() {
		return this.identifier.hashCode();
	}
}
