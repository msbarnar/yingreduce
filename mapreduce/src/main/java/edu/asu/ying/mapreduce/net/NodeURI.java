package edu.asu.ying.mapreduce.net;

/**
 * {@code NodeURI} uniquely identifies a node in the network, but does not necessarily indicate
 * the location of the node.
 * </p>
 * An example in the Kademlia network is the node's XOR key: the key does not specify the physical
 * address of an endpoint, but it can be used to look for that endpoint by querying other nodes.
 */
public interface NodeURI {

  @Override
  String toString();
  @Override
  boolean equals(Object o);
  @Override
  int hashCode();
}
