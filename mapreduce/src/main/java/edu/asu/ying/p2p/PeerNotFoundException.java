package edu.asu.ying.p2p;

import java.io.IOException;
import java.net.URI;

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
