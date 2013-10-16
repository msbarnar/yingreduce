package edu.asu.ying.p2p;

import java.net.URI;
import java.rmi.RemoteException;

/**
 *
 */
public class PeerNotFoundException extends RemoteException {

  public PeerNotFoundException(String name) {
    super(name);
  }

  public PeerNotFoundException(PeerName name) {
    this(name.toString());
  }

  public PeerNotFoundException(URI uri) {
    this(uri.toString());
  }
}
