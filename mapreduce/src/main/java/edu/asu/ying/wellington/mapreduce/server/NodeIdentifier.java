package edu.asu.ying.wellington.mapreduce.server;

import edu.asu.ying.wellington.AbstractIdentifier;

/**
 *
 */
public final class NodeIdentifier extends AbstractIdentifier {

  public static NodeIdentifier forString(String id) {
    return new NodeIdentifier(id);
  }

  private static final long SerialVersionUID = 1L;

  private NodeIdentifier(String id) {
    super(id);
  }
}
