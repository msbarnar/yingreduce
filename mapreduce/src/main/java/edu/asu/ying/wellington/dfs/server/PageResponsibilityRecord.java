package edu.asu.ying.wellington.dfs.server;

import edu.asu.ying.wellington.RemoteNode;

/**
 * {@code PageResponsibilityRecord} tracks a node which is responsible for (carries a copy of,
 * and is viable for task regarding) a page.
 */
public final class PageResponsibilityRecord {

  // The node responsible for the page
  private final RemoteNode node;

  private int cycleLastSeen;

  public PageResponsibilityRecord(RemoteNode node) {
    this.node = node;
  }

  public RemoteNode getNode() {
    return node;
  }

  public boolean sawThisCycle(int cycle) {
    return cycleLastSeen >= cycle;
  }

  /**
   * Updates the time last seen to the current time.
   */
  public void sawNode(int cycle) {
    cycleLastSeen = cycle;
  }
}
