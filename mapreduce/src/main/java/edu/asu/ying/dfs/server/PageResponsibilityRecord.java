package edu.asu.ying.dfs.server;

import javax.annotation.Nullable;

import edu.asu.ying.dfs.PageName;

/**
 * {@code PageResponsibilityRecord} tracks a node which is responsible for (carries a copy of,
 * and is viable for task regarding) a page.
 */
public final class PageResponsibilityRecord {

  // The node responsible
  private final String nodeName;
  // The node reference, if available
  @Nullable
  private RemoteNode node;
  // The page for which the node is responsible
  private final PageName pageId;

  public PageResponsibilityRecord(PageName pageId, String nodeName) {
    this.pageId = pageId;
    this.nodeName = nodeName;
  }

  public PageResponsibilityRecord(PageName pageId, String nodeName,
                                  @Nullable RemoteNode node) {
    this(pageId, nodeName);
    this.node = node;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNode(@Nullable RemoteNode node) {
    this.node = node;
  }

  @Nullable
  public RemoteNode getNode() {
    return node;
  }

  public PageName getPageId() {
    return pageId;
  }
}
