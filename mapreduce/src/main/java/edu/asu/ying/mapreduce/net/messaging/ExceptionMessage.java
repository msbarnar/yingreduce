package edu.asu.ying.mapreduce.net.messaging;


/**
 * {@link ExceptionMessage} wraps a {@link java.lang.Throwable} in a message for responding to
 * requests.
 */
public class ExceptionMessage
    extends MessageBase {

  private final static long serialVersionUID = 1L;

  public ExceptionMessage(final String id, final Throwable cause) {
    super(id);
    this.setException(cause);
  }

  public ExceptionMessage(final Throwable cause) {
    this("", cause);
  }
}
