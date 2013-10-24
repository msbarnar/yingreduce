package edu.asu.ying.wellington.dfs.server;

/**
 * {@code PageTransferResult} is the response of one node to another following the completed
 * transmission of a {@link PageTransfer}; completion in this context does not mean the page was
 * transferred, only that the transfer metadata was transferred.
 */
public enum PageTransferResult {
  Accepted,       // The remote node accepted the transfer completely and should be added to the
  // list of container nodes for that page.
  Duplicate,      // The remote node already has the page and should also be added to the list of
  // container nodes for that page.
  TryAgain,       // The transfer was interrupted or the remote node lost the page.
  OutOfCapacity,  // The remote node can't accept any more pages.
  ChecksumFailed, // The data were corrupted in the transfer
}
