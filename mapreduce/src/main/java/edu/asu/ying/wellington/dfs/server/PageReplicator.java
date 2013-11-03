package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

/**
 * The {@code PageReplicator} is responsible for keeping track of which nodes share copies of our
 * pages and staying in touch with them to ensure they stay alive. If any of those nodes goes down,
 * the replicator will communicate with the other nodes in the set to decide who should replicate
 * the lost page to new nodes.
 */
public final class PageReplicator implements Sink<PageTransfer>, QueueProcessor<PageTransfer> {

  private static final Logger log = Logger.getLogger(PageReplicator.class.getName());

  // The time a node is allowed to not respond before it is considered dead and its pages are
  // re-replicated
  private static final long NODE_TIMEOUT_MS = 3 * 60 * 1000;    // 3 minutes

  // For finding the next responsible node
  private final NodeLocator locator;

  // Pass to nodes we ping so they can see us, too
  private final RemoteNode localNodeProxy;

  // For retrieving pages to be replicated
  private final Persistence pageCache;

  // The cycle time slot for checking nodes are still up
  private int currentPingCycle = 0;

  // The list of pages that have yet to be replicated.
  private final QueueExecutor<PageTransfer> toBeReplicated = new DelegateQueueExecutor<>(this);

  /**
   * Keeps a record of all the other nodes responsible for the same pages for which we are
   * responsible.
   */
  private final Map<PageName, List<PageResponsibilityRecord>> responsibleNodes = new HashMap<>();

  /**
   * Records the number of nodes which have timed out for each page
   */
  private final Map<PageName, Integer> timedOutPages = new HashMap<>();


  @Inject
  private PageReplicator(@Local RemoteNode localNodeProxy,
                         @CachePersistence Persistence pageCache,
                         NodeLocator locator) {
    this.localNodeProxy = localNodeProxy;
    this.pageCache = pageCache;
    this.locator = locator;
  }

  /**
   * Queues the page for replication. The page must be persisted in cache; if not, an IOException
   * is thrown.
   */
  @Override
  public void accept(PageTransfer transfer) throws IOException {
    if (!pageCache.hasPage(transfer.page.name())) {
      throw new IOException("Page is not cached; cannot be replicated");
    }
    toBeReplicated.add(transfer);

    List<PageResponsibilityRecord> records = responsibleNodes.get(transfer.page.name());
    if (records == null) {
      records = new ArrayList<>();
      responsibleNodes.put(transfer.page.name(), records);
    }
    // Get the responsibility table from the previous node and add it to our own
    List<RemoteNode> nodes = transfer.sendingNode
        .getDFSService()
        .getResponsibleNodesFor(transfer.page.name());

    for (RemoteNode node : nodes) {
      // Don't include ourselves
      if (!node.equals(localNodeProxy)) {
        boolean alreadyAdded = false;
        for (PageResponsibilityRecord record : records) {
          // Don't add nodes already in the table
          if (record.getNode().equals(node)) {
            alreadyAdded = true;
            break;
          }
        }
        if (!alreadyAdded) {
          // Add the node as a responsible node for this page
          records.add(new PageResponsibilityRecord(node));
        }
      }
    }
  }

  public List<RemoteNode> getResponsibleNodesFor(PageName name) {
    List<RemoteNode> nodes = new ArrayList<>();
    // Include ourselves as a responsible node as seen by other nodes
    nodes.add(localNodeProxy);

    List<PageResponsibilityRecord> records = responsibleNodes.get(name);
    if (records != null) {
      for (PageResponsibilityRecord record : records) {
        nodes.add(record.getNode());
      }
    }
    return nodes;
  }

  /**
   * Replicates a single page to the next closest node, which is placed in the responsibility
   * table for this page.
   */
  @Override
  public void process(PageTransfer transfer) throws Exception {

  }

  /**
   * Updates the last seen cycle for every page for which this node is responsible
   */
  public boolean ping(RemoteNode node) {
    for (List<PageResponsibilityRecord> records : responsibleNodes.values()) {
      for (PageResponsibilityRecord record : records) {
        if (record.getNode().equals(node)) {
          record.sawNode(currentPingCycle);
        }
      }
    }
    return true;
  }

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
            // Ping with our own node proxy so they see us, too
            if (record.getNode().getDFSService().ping(localNodeProxy)) {
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
