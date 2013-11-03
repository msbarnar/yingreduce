package edu.asu.ying.wellington.dfs.server;

import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.wellington.dfs.PageName;

/**
 * The {@code PageReplicator} is responsible for keeping track of which nodes share copies of our
 * pages and staying in touch with them to ensure they stay alive. If any of those nodes goes down,
 * the replicator will communicate with the other nodes in the set to decide who should replicate
 * the lost page to new nodes.
 */
public final class PageReplicator {

  /**
   * Keeps a record of all the other nodes responsible for the same pages for which we are
   * responsible.
   */
  private final Map<PageName, PageResponsibilityRecord> responsibleNodes = new HashMap<>();

}
