package edu.asu.ying.wellington.dfs.server;

import javax.annotation.Nullable;

import edu.asu.ying.wellington.RemoteNode;

/**
 * {@code PageResponsibilityRecord} tracks a node which is responsible for (carries a copy of,
 * and is viable for task regarding) a page.
 */
public final class PageResponsibilityRecord {

  // The node responsible
  private final String nodeName;
  // The node reference, if available
  @Nullable
  private final RemoteNode node;

  private long timeLastSeen;

  public PageResponsibilityRecord(String nodeName) {
    this.nodeName = nodeName;
    this.node = null;
    sawNode();
  }

  public PageResponsibilityRecord(String nodeName, @Nullable RemoteNode node) {
    this.nodeName = nodeName;
    this.node = node;
    sawNode();
  }

  public String getNodeName() {
    return nodeName;
  }

  @Nullable
  public RemoteNode getNode() {
    return node;
  }

  public boolean isTimedOut(long timeout) {
    return (System.currentTimeMillis() - timeLastSeen) >= timeout;
  }

  /**
   * Updates the time last seen to the current time.
   */
  public void sawNode() {
    timeLastSeen = System.currentTimeMillis();
  }
}
