package edu.asu.ying.p2p;

import java.net.URI;
import java.rmi.RemoteException;

/**
 *
 */
public class PeerNotFoundException extends RemoteException {

  public PeerNotFoundException(String identifier) {
    super(identifier);
  }

  public PeerNotFoundException(PeerIdentifier identifier) {
    this(identifier.toString());
  }

  public PeerNotFoundException(URI uri) {
    this(uri.toString());
  }
}
