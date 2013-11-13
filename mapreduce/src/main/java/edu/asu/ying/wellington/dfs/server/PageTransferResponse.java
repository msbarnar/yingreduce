package edu.asu.ying.wellington.dfs.server;

import com.healthmarketscience.rmiio.RemoteOutputStream;

import java.io.Serializable;

/**
 * Signals the action taken by a remote node when offered a {@link PageTransfer}.
 * If the response is {@code Accepting}, the remote node will begin downloading the page and
 * further response should be expected.
 */
public final class PageTransferResponse implements Serializable {

  private static final long serialVersionUID = 1L;

  public final Status status;
  public final Throwable exception;
  public final RemoteOutputStream outputStream;

  /**
   * Initializes the response with no output stream; {@code status} should be something other than
   * {@link Status.Accepting}.
   */
  public PageTransferResponse(Status status) {
    if (status == Status.Accepting) {
      throw new IllegalArgumentException("If the node is accepting the page it must provide a"
                                         + " remote output stream for the page to be written to.");
    }
    this.status = status;
    this.outputStream = null;
    this.exception = null;
  }

  /**
   * Sets the response status to {@link Status.Accepting}.
   */
  public PageTransferResponse(RemoteOutputStream outputStream) {
    status = Status.Accepting;
    this.outputStream = outputStream;
    this.exception = null;
  }

  /**
   * Returns an exception, setting the status to {@link Status.Exception}.
   */
  public PageTransferResponse(Throwable exception) {
    this.status = Status.Exception;
    this.exception = exception;
    this.outputStream = null;
  }

  /**
   * Indicates the remote node's response to an offered {@link PageTransfer}.
   */
  public static enum Status {
    /**
     * The remote node is accepting the page and has provided a remote output stream to which the
     * page should be written.
     */
    Accepting,
    /**
     * The remote node already has the page and should also be added to the responsible node table
     * for that page.
     */
    Duplicate,
    /**
     * The remote node can't accept any more pages, but should be given a reference to the node
     * to which the page is distributed so that there are no missing links in the page's
     * responsibility chain.
     */
    OutOfCapacity,
    /**
     * The load on the remote node is currently too high to respond; try again soon.
     */
    Overloaded,
    /**
     * The page transfer was denied because of an exception. This is where security denials are
     * returned.
     */
    Exception
  }
}
