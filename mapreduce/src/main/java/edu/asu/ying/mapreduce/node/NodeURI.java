package edu.asu.ying.mapreduce.node;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * {@code NodeURI} uniquely identifies a node in the network, but does not indicate the
 * location of the node.
 * </p>
 * An example in the Kademlia network is the node's XOR key: the key does not specify the physical
 * address of an endpoint, but it can be used to look for that endpoint by querying known physical
 * nodes.
 */
public interface NodeURI extends Serializable {

  @Nonnull
  String getKey();
}
