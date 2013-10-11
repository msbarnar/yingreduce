package edu.asu.ying.wellington.mapreduce.net;

import edu.asu.ying.wellington.Identifier;

/**
 *
 */
public final class NodeIdentifier extends Identifier {

  private static final long SerialVersionUID = 1L;

  private static final String NODE_PREFIX = "node";

  public NodeIdentifier(String id) {
    super(NODE_PREFIX, id);
  }
}
