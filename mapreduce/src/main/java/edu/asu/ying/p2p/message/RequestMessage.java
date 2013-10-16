package edu.asu.ying.p2p.message;

import edu.asu.ying.p2p.PeerName;

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

  public RequestMessage(String tag, PeerName destinationNode) {
    super(tag, destinationNode);
  }

  public RequestMessage(String id, String tag,
                        PeerName destinationNode) {

    super(id, tag, destinationNode);
  }
}
