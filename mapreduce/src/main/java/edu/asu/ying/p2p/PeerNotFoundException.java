package edu.asu.ying.p2p;

import java.net.URI;
import java.rmi.RemoteException;

/**
 *
 */
public final class PeerNotFoundException extends RemoteException {

  public PeerNotFoundException(final PeerIdentifier address) {
    super(address.toString());
  }

  public PeerNotFoundException(final URI uri) {
    super(uri.toString());
  }
}
