package edu.asu.ying.mapreduce.net;

/**
 * {@code NodeURN} specifies the uniquely identifying key of a node, e.g. the XOR key in a
 * Kademlia network, but does not specify the physical location of the node.
 * </p>
 * In a Kademlia network, the physical node can be found by querying other known nodes for the
 * node's URN.
 */
public interface NodeURN extends NodeURI {
}
