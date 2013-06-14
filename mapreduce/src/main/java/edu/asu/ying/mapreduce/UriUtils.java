package edu.asu.ying.mapreduce;

import com.google.common.base.Charsets;

import java.math.BigInteger;


/**
 *
 */
public final class UriUtils
{
	/**
	 * Returns a URI compatible encoding of the host string.
	 */
	public final static String encodeHost(final String host) {
		return String.format("%x", new BigInteger(1, host.getBytes(Charsets.UTF_8)));
	}
}
