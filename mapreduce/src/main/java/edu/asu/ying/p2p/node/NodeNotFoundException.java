package edu.asu.ying.p2p.node;

import java.io.IOException;
import java.net.URI;

import edu.asu.ying.p2p.NodeIdentifier;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(final NodeIdentifier address) {
    super(address.toString());
  }

  public NodeNotFoundException(final URI uri) {
    super(uri.toString());
  }
}
