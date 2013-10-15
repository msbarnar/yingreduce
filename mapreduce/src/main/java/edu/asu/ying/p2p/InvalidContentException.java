package edu.asu.ying.p2p;

import java.io.IOException;
import java.io.Serializable;


/**
 * Signals to a message sender that its message was malformed. <p> In practice, this generally means
 * the content did not derive from {@link edu.asu.ying.p2p.message.Message}.
 */
public class InvalidContentException extends IOException {

  public InvalidContentException() {
    super("The message received was of an unexpected or unknown format.");
  }

  public InvalidContentException(final String message) {
    super(message);
  }

  public InvalidContentException(final Serializable content) {
    this("The message received was of an unexpected or unknown format: "
             .concat(content.toString()));
  }

  public InvalidContentException(final Class<?> expected, final Serializable content) {
    this(String.format("The message received was of an unexpected or unknown format.\n"
                       + "Expected '%s', but got '%s'", expected.toString(), content.toString()));
  }
}
