package edu.asu.ying.wellington.dfs.server;

/**
 * Signals the end result of a completed {@link PageTransfer}.
 */
public enum PageTransferResult {
  Accepted,       // The page was received and stored and the node can be considered to be carrying.
  ChecksumFailed, // The page data were corrupt.
  Invalid,        // The data were received, but were not a page.
  OtherError,     // The transfer failed due to no fault of the sender
}
