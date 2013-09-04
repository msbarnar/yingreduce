package edu.asu.ying.p2p.io.message;

import edu.asu.ying.p2p.NodeIdentifier;

/**
 *
 */
public class RequestMessage extends MessageBase {

  public RequestMessage(String tag) {
    super(tag);
  }

  public RequestMessage(String id, String tag) {
    super(id, tag);
  }

  public RequestMessage(String tag, NodeIdentifier destinationNode) {
    super(tag, destinationNode);
  }

  public RequestMessage(String id, String tag,
                        NodeIdentifier destinationNode) {
    
    super(id, tag, destinationNode);
  }
}
