package edu.asu.ying.p2p.io;

import java.io.IOException;


/**
 * Signals to a message sender that its message was malformed. <p> In practice, this generally means
 * the content did not derive from {@link edu.asu.ying.p2p.io.message.Message}.
 */
public class InvalidContentException
    extends IOException {

  public InvalidContentException() {
  }

  public InvalidContentException(final String detail) {
    super(detail);
  }
}
