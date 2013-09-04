package edu.asu.ying.p2p.node;

import java.io.IOException;

/**
 *
 */
public class NodeNotFoundException extends IOException {

  public NodeNotFoundException(final NodeURL address) {
    super(address.toString());
  }
}
