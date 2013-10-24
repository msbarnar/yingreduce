package edu.asu.ying.wellington.dfs.server;

/**
 * Signals the action taken by a remote node when offered a {@link PageTransfer}.
 * If the response is {@code Accepting}, the remote node will begin downloading the page and
 * further
 * response should be expected.
 */
public enum PageTransferResponse {
  Accepting,      // The remote node accepted the transfer completely and should be added to the
  // list of container nodes for that page.
  Duplicate,      // The remote node already has the page and should also be added to the list of
  // container nodes for that page.
  OutOfCapacity,  // The remote node can't accept any more pages.
  Overloaded,     // The remote node currently has too many page fetches queued to accept more.
  // Try again soon.
}
