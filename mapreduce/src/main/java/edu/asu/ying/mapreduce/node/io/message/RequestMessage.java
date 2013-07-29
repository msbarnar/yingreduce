package edu.asu.ying.mapreduce.node.io.message;

import edu.asu.ying.mapreduce.node.NodeURI;

/**
 *
 */
public class RequestMessage extends MessageBase {

  private static final long SerialVersionUID = 1L;

  public RequestMessage(final NodeURI destinationNode) {
    super(destinationNode);
  }
}
