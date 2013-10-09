package edu.asu.ying.p2p.net;

import edu.asu.ying.p2p.PeerIdentifier;

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

  public RequestMessage(String tag, PeerIdentifier destinationNode) {
    super(tag, destinationNode);
  }

  public RequestMessage(String id, String tag,
                        PeerIdentifier destinationNode) {

    super(id, tag, destinationNode);
  }
}
