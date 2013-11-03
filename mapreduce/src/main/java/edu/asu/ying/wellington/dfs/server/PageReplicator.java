package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageName;

/**
 * The {@code PageReplicator} is responsible for keeping track of which nodes share copies of our
 * pages and staying in touch with them to ensure they stay alive. If any of those nodes goes down,
 * the replicator will communicate with the other nodes in the set to decide who should replicate
 * the lost page to new nodes.
 */
public final class PageReplicator {

  private static final Logger log = Logger.getLogger(PageReplicator.class.getName());

  // The time a node is allowed to not respond before it is considered dead and its pages are
  // re-replicated
  private static final long NODE_TIMEOUT_MS = 3 * 60 * 1000;    // 3 minutes

  // Pass to nodes we ping so they can see us, too
  private final RemoteNode localNodeProxy;

  // The cycle time slot for checking nodes are still up
  private int currentPingCycle = 0;

  @Inject
  private PageReplicator(@Local RemoteNode localNodeProxy) {
    this.localNodeProxy = localNodeProxy;
  }

  /**
   * Keeps a record of all the other nodes responsible for the same pages for which we are
   * responsible.
   */
  private final Map<PageName, List<PageResponsibilityRecord>> responsibleNodes = new HashMap<>();
  /**
   * Records the number of nodes which have timed out for each page
   */
  private final Map<PageName, Integer> timedOutPages = new HashMap<>();

  /**
   * Scans the page responsibility table and notes any pages with timed out nodes to be replicated.
   */
  private void checkTimedOutNodes() {
    for (Map.Entry<PageName, List<PageResponsibilityRecord>> entry : responsibleNodes.entrySet()) {
      PageName pageName = entry.getKey();
      // Iterate over all nodes responsible for each page and check their time
      Iterator<PageResponsibilityRecord> records = entry.getValue().iterator();
      while (records.hasNext()) {
        PageResponsibilityRecord record = records.next();
        // Only ping nodes we haven't seen (if a node pings us, we saw it)
        if (!record.sawThisCycle(currentPingCycle)) {
          try {
            if (record.getNode().getDFSService().ping()) {
              record.sawNode(currentPingCycle);
            } else {
              // Increment the number of timed out nodes for this page
              addTimedOutNode(pageName);
              // Remove this node from the responsibility table
              records.remove();
            }
          } catch (RemoteException e) {
            // Increment the number of timed out nodes for this page
            addTimedOutNode(pageName);
            // Remove this node from the responsibility table
            records.remove();
          }
        }
      }
    }
    // Advance one time slot
    ++currentPingCycle;
  }

  /**
   * Increments the number of timed out nodes for this page by one.
   */
  private void addTimedOutNode(PageName page) {
    // Prevent a race condition where a page's number of timed out nodes is incremented after
    // the timeout is dealt with but before the number is decremented, resulting in the value of
    // timedOutPages being one greater than it should be
    synchronized (timedOutPages) {
      Integer numTimedOut = timedOutPages.get(page);
      if (numTimedOut == null) {
        numTimedOut = 1;
        timedOutPages.put(page, numTimedOut);
      } else {
        timedOutPages.put(page, ++numTimedOut);
      }
    }
  }
}
