package edu.asu.ying.p2p.rmi;

import java.rmi.RemoteException;

/**
 *
 */
public class RemoteImportException extends RemoteException {

  public RemoteImportException(final Throwable cause) {
    super("Failed to import a reference to the remote object", cause);
  }
}
