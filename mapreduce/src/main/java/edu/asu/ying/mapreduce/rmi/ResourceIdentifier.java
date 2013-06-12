package edu.asu.ying.mapreduce.rmi;

import java.io.IOException;

/**
 * A {@link ResourceIdentifier} locates a resource in a given system.
 * <p>
 * In the case of a Kademlia network, the resource identifier is the unique key that
 * locates a value in the network.
 */
public interface ResourceIdentifier {
	/**
	 * Converts the identifier to a platform-independent byte array representation.
	 * @return a platform-independent representation of the identifier.
	 * @throws IOException if converting the identifier fails.
	 */
	public byte[] toByteArray() throws IOException;
}
