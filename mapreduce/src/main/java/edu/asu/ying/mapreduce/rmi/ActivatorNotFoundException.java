package edu.asu.ying.mapreduce.rmi;

/**
 * Thrown when a request for an {@link Activator} reference from a remote node fails.
 */
public class ActivatorNotFoundException extends Exception {

  public ActivatorNotFoundException(final Throwable cause) {
    super(cause);
  }
  public ActivatorNotFoundException(final String detail) {
    super(detail);
  }

  public ActivatorNotFoundException(final Throwable cause, final String detail) {
    super(detail, cause);
  }
}
