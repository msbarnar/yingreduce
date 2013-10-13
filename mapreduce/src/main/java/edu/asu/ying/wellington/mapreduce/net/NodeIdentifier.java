package edu.asu.ying.wellington.mapreduce.net;

import edu.asu.ying.wellington.Identifier;

/**
 *
 */
public final class NodeIdentifier extends Identifier {

  public static NodeIdentifier forString(String id) {
    return new NodeIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private static final String NODE_PREFIX = "node";

  private NodeIdentifier(String id) {
    super(NODE_PREFIX, id);
  }
}
