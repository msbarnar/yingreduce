package edu.asu.ying.p2p.peer;

import java.io.IOException;
import java.net.URI;

import edu.asu.ying.p2p.PeerIdentifier;

/**
 *
 */
public class PeerNotFoundException extends IOException {

  public PeerNotFoundException(final PeerIdentifier address) {
    super(address.toString());
  }

  public PeerNotFoundException(final URI uri) {
    super(uri.toString());
  }
}
