package edu.asu.ying.mapreduce.rpc.net;


/**
 * Signals that no node matching a given key was found in the kademlia network.
 * <p>
 * The kademlia algorithm returns nearest nodes, so this should only apply in situations
 * where a specific node was searched for and a nearest neighbor was unsuitable.
 */
public final class NodeNotFoundException
	extends RuntimeException
{
	private static final long serialVersionUID = -2305697664564522406L;
	
	private final NetworkKey key;
	
	public NodeNotFoundException() {
		super();
		this.key = null;
	}
	public NodeNotFoundException(final Throwable cause) {
		super(cause);
		this.key = null;
	}
	/**
	 * Specifies that no node was found for a specific {@link NetworkKey}.
	 * @param key the key for which no node could be found.
	 */
	public NodeNotFoundException(final NetworkKey key) {
		this.key = key;
	}
	
	public final NetworkKey getKey() { return this.key; }
}
