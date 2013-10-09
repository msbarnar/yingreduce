package edu.asu.ying.p2p.net;

import java.io.IOException;


/**
 * Signals that an object was waiting for a response from a remote host and didn't get one due to
 * either timeout, interruption, or exception.
 */
public class NoResponseException
    extends IOException {

  public NoResponseException() {
  }

  public NoResponseException(final Throwable cause) {
    super(cause);
  }
}
