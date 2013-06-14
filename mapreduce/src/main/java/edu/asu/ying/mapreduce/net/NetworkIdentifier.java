package edu.asu.ying.mapreduce.net;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;
import java.util.Stack;


/**
 * Identifies an item on the network.
 */
public final class NetworkIdentifier
{
	private enum Part {
		Scheme,
		Address,
		Path
	}
	private final String identifier;
	private final List<String> parts;
	private final String host;
	private final int port;

	public NetworkIdentifier(final String identifier) {
		this.identifier = identifier;
		this.parts = Lists.newArrayList(Splitter.on('/').trimResults().split(identifier));
		final String address = this.getNullable(Part.Address);

		String host = address;
		int port = -1;
		if (address != null) {
			final List<String> hostParts
					= Lists.newArrayList(Splitter.on(':').trimResults().split(address));
			if (hostParts.size() > 1) {
				try {
					port = Integer.parseInt(hostParts.get(hostParts.size()-1));
					host = hostParts.get(0);
				} catch (final NumberFormatException e) {
				}
			}
		}
		this.host = host;
		this.port = port;
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
}
