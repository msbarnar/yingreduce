package edu.asu.ying.wellington.dfs.server;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;

import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.PersistenceConnector;

/**
 * The {@code PageReplicator} is responsible for keeping track of which nodes share copies of our
 * pages and staying in touch with them to ensure they stay alive. If any of those nodes goes down,
 * the replicator will communicate with the other nodes in the set to decide who should replicate
 * the lost page to new nodes.
 */
// FIXME: This class is a joke
public final class PageReplicator implements Sink<PageTransfer>, QueueProcessor<PageTransfer> {

  private static final Logger log = Logger.getLogger(PageReplicator.class.getName());

  // The time a node is allowed to not respond before it is considered dead and its pages are
  // re-replicated
  private static final long NODE_TIMEOUT_MS = 5 * 1000;    // 10 seconds

  // For finding the next responsible node
  private final NodeLocator locator;

  // Pass to nodes we ping so they can see us, too
  private final Provider<RemoteNode> localNodeProxyProvider;

  private final Provider<Sink<PageData>> distributionSinkProvider;

  // For retrieving pages to be replicated
  private final PersistenceConnector pageCache;

  // The cycle time slot for checking nodes are still up
  private final AtomicInteger currentPingCycle = new AtomicInteger(0);

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

  private final Timer timer;


  @Inject
  private PageReplicator(@Local Provider<RemoteNode> localNodeProxyProvider,
                         @CachePersistence PersistenceConnector pageCache,
                         NodeLocator locator,
                         @Distribution Provider<Sink<PageData>> distributionSinkProvider) {

    this.localNodeProxyProvider = localNodeProxyProvider;
    this.pageCache = pageCache;
    this.locator = locator;
    this.distributionSinkProvider = distributionSinkProvider;
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        checkTimedOutNodes();
      }
    }, NODE_TIMEOUT_MS, NODE_TIMEOUT_MS);

    toBeReplicated.start();
  }

  /**
   * Queues the page for replication. The page must be persisted in cache; if not, an IOException
   * is thrown.
   */
  @Override
  public void accept(PageTransfer transfer) throws IOException {
    if (!pageCache.exists(transfer.page.name())) {
      throw new IOException("Page is not cached; cannot be replicated");
    }

    String localName = locator.local().getName();

    log.info(
        String.format("Page %s showed up at replicator on %s", transfer.page.name(), localName));

    // Start with our list of responsible nodes for this page; if we don't know any, just start
    // with us
    List<PageResponsibilityRecord> records = responsibleNodes.get(transfer.page.name());
    if (records == null) {
      records = new ArrayList<>();
      records.add(new PageResponsibilityRecord(locator.local()));
      responsibleNodes.put(transfer.page.name(), records);
    }

    log.info(String.format("[%s] I know that %d nodes know about %s", localName,
                           records.size(), transfer.page.name()));

    if (records.size() >= 3) {
      log.info(String.format("[%s] That's enough", localName));
      return;
    }

    log.info(String.format("[%s] That's not enough, let me ask around...", localName));

    // Get the responsibility table from the previous node and add it to our own
    List<RemoteNode> nodes = transfer.sendingNode
        .getDFSService()
        .getResponsibleNodesFor(transfer.page.name());

    RemoteNode localNodeProxy = localNodeProxyProvider.get();
    for (RemoteNode node : nodes) {
      // Don't ask ourselves
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

    log.info(String.format("[%s] After asking, now I know that %d nodes know about %s",
                           locator.local().getName(),
                           records.size(), transfer.page.name()));

    // Don't replicate the page more than 3 times
    if (records.size() < 3) {
      toBeReplicated.add(transfer);
    }
  }

  /**
   * Returns the nodes this node knows are responsible for {@code name}, including itself.
   */
  public List<RemoteNode> getResponsibleNodesFor(PageName name) {
    List<RemoteNode> nodes = new ArrayList<>();
    // Include ourselves as a responsible node as seen by other nodes
    nodes.add(localNodeProxyProvider.get());

    List<PageResponsibilityRecord> records = responsibleNodes.get(name);
    if (records != null) {
      for (PageResponsibilityRecord record : records) {
        nodes.add(record.getNode());
      }
    }
    return nodes;
  }

  private void addResponsibleNode(PageName page, RemoteNode node) {
    List<PageResponsibilityRecord> records = responsibleNodes.get(page);
    if (records == null) {
      records = new ArrayList<>();
      responsibleNodes.put(page, records);
    }
    records.add(new PageResponsibilityRecord(node));
  }

  /**
   * Replicates a single page to the next closest node, which is placed in the responsibility
   * table for this page.
   */
  @Override
  public void process(PageTransfer transfer) throws Exception {
    log.info("Replicating page " + transfer.page.name());

    // Find pages that SHOULD be responsible for this page
    List<RemoteNode> nodes = locator.find(transfer.page.name().toString(), 5);
    List<RemoteNode> responsibleNodes = getResponsibleNodesFor(transfer.page.name());

    // Look for nodes to replicate to
    for (RemoteNode node : nodes) {
      // The first node for which this is false gets the page
      boolean nodeAlreadyResponsible = false;
      String nodeName = node.getName();
      // Check if this node is in the responsible nodes
      // FIXME: WOW is this inefficient
      for (RemoteNode alreadyHave : responsibleNodes) {
        String alreadyHaveName = alreadyHave.getName();
        if (nodeName.equals(alreadyHaveName)) {
          nodeAlreadyResponsible = true;
          break;
        }
      }
      // Pick this node if not in the responsibility table
      if (!nodeAlreadyResponsible) {
        log.info("Found a node for page " + transfer.page.name() + ": " + node.getName());
        // Read the page data from cache and prepare a transfer for it
        InputStream istream = pageCache.getInputStream(transfer.page.name());
        PageData pageData = new PageData(transfer.page, ByteStreams.toByteArray(istream));
        // Specify where to send it
        pageData.setDestination(node);
        distributionSinkProvider.get().accept(pageData);
        addResponsibleNode(transfer.page.name(), node);
        // Stop looking for nodes to replicate to
        return;
      }
    }
    log.info(transfer.page.name() + " already sufficiently replicated");
  }

  /**
   * Updates the last seen cycle for every page for which this node is responsible
   */
  public void ping(RemoteNode node) {
    for (List<PageResponsibilityRecord> records : responsibleNodes.values()) {
      for (PageResponsibilityRecord record : records) {
        if (record.getNode().equals(node)) {
          record.sawNode(currentPingCycle.get());
        }
      }
    }
  }

  /**
   * Scans the page responsibility table and notes any pages with timed out nodes to be replicated.
   */
  private void checkTimedOutNodes() {
    RemoteNode localNodeProxy = null;
    try {
      localNodeProxy = localNodeProxyProvider.get();
    } catch (ProvisionException e) {
      // Happens when the node shuts down
      timer.cancel();
      return;
    }

    // FIXME: Integer overflow will break this
    int currentCycle = currentPingCycle.getAndIncrement();

    for (Map.Entry<PageName, List<PageResponsibilityRecord>> entry : responsibleNodes.entrySet()) {
      PageName pageName = entry.getKey();
      // Iterate over all nodes responsible for each page and check their time
      Iterator<PageResponsibilityRecord> records = entry.getValue().iterator();
      while (records.hasNext()) {
        PageResponsibilityRecord record = records.next();
        // Only ping nodes we haven't seen (if a node pings us, we saw it)
        if (!record.sawThisCycle(currentCycle)) {
          // Ping with our own node proxy so they see us, too
          try {
            record.getNode().getDFSService().ping(localNodeProxy);
            record.sawNode(currentCycle);
            //log.debug("Saw node " + record.getNode().getName());
          } catch (NullPointerException | RemoteException e) {
            // Increment the number of timed out nodes for this page
            addTimedOutNode(pageName);
            // Remove this node from the responsibility table
            records.remove();
          }
        }
      }
    }
  }

  /**
   * Increments the number of timed out nodes for this page by one.
   */
  private void addTimedOutNode(PageName page) {
    // Prevent a race condition where a page's number of timed out nodes is incremented after
    // the timeout is dealt with but before the number is decremented, resulting in the value of
    // timedOutPages being one greater than it should be
    log.warn("Node timed out for page " + page);

    PageTransfer transfer = null;
    try {
      PageData data = PageData.readFrom(new DataInputStream(pageCache.getInputStream(page)));
      transfer = new PageTransfer(locator.local(), 3, data.header().getPage());
    } catch (IOException e) {
      log.error("Tried to replicate timed out page, but exception getting data from cache", e);
    }
    if (transfer != null) {
      toBeReplicated.add(transfer);
    }
  }
}
