package edu.asu.ying.wellington.dfs.server;

import java.io.Serializable;

/**
 * {@code PageTransferResult} is the response of one node to another following the completed
 * transmission of a {@link PageTransfer}. Completion in this context does not mean the page was
 * transferred, only that the transfer metadata was transferred.
 */
public final class PageTransferResult implements Serializable {

  private static final long SerialVersionUID = 1L;

  private final PageTransferStatus status;

  public PageTransferResult(PageTransferStatus status) {
    this.status = status;
  }

  public PageTransferStatus getStatus() {
    return status;
  }

  private static enum PageTransferStatus {
    Accepted,
    Incomplete,
    OutOfCapacity,
    Duplicate
  }
}
