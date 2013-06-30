package edu.asu.ying.mapreduce.net;

import edu.asu.ying.mapreduce.net.resource.ResourceIdentifier;

/**
 * {@code NodeIdentifier} is a {@link ResourceIdentifier} that specifically identifies a node
 * in the network by its Kademlia key.
 */
public class NodeIdentifier extends ResourceIdentifier {

  private static final String NODE_SCHEME = "node";

  public NodeIdentifier(final String key) {
    super(NODE_SCHEME, key, -1);
  }
}
