package edu.asu.ying.mapreduce.net;

import java.io.Serializable;

/**
 * {@code NodeURI} uniquely identifies a node in the network, but does not indicate the
 * location of the node.
 * </p>
 * An example in the Kademlia network is the node's XOR key: the key does not specify the physical
 * address of an endpoint, but it can be used to look for that endpoint by querying known physical
 * nodes.
 */
public interface NodeURI extends Serializable {

  @Override
  String toString();
  @Override
  boolean equals(Object o);
  @Override
  int hashCode();
}
