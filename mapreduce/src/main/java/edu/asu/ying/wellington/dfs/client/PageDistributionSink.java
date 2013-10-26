package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.SerializedReadablePage;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.dfs.server.PageTransferResult;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageDistributionSink} distributes accepted pages to their initial peers on the network.
 */
public final class PageDistributionSink
    implements Sink<SerializedReadablePage>, QueueProcessor<SerializedReadablePage> {

  private static final Logger log = Logger.getLogger(PageDistributionSink.class.getName());

  public static final String PROPERTY_PAGE_REPLICATION = "dfs.page.replication";

  private final NodeLocator locator;

  private int pageReplicationFactor;

  private final DelegateQueueExecutor<SerializedReadablePage> pageQueue
      = new DelegateQueueExecutor<>(this);
  private final Map<String, SerializedReadablePage> inProgressTransfers = new HashMap<>();

  /**
   * Creates the distribution sink.
   *
   * @param locator           The class used for locating nodes by a search string.
   * @param replicationFactor The number of nodes on which a copy of the page will be stored.
   */
  @Inject
  private PageDistributionSink(NodeLocator locator,
                               @Named(PROPERTY_PAGE_REPLICATION) int replicationFactor) {

    this.locator = locator;
    if (replicationFactor < 1) {
      throw new IllegalArgumentException(PROPERTY_PAGE_REPLICATION.concat(" must be >0"));
    }
    this.pageReplicationFactor = replicationFactor;
  }

  /**
   * Adds a page to the queue for distribution to its initial node.
   */
  @Override
  public void accept(final SerializedReadablePage page) throws IOException {
    pageQueue.add(page);
  }

  /**
   * Distributes a page to its initial node in the following steps:
   * <ol>
   * <li>Locates the node closest to the page's ID</li>
   * <li>Wraps the page's data in a {@link com.healthmarketscience.rmiio.RemoteInputStream}</li>
   * <li>Constructs a {@link PageTransfer} wrapping the page's metadata and the input stream</li>
   * <li>Offers the transfer to the remote node</li>
   * </ol>
   * <ul>
   * <li>If the remote node accepts the transfer, the transfer is placed in the {@code in-progress}
   * queue. The remote node should notify this node on completion of the transfer so it can be
   * removed from the queue.</li>
   * <li>If the remote node refuses the transfer because it is overloaded, the transfer is put back
   * on the transfer queue to be started at a later time.</li>
   * <li>If the remote node refuses the transfer because it is over capacity, the next closest node
   * is selected and the process repeats. If that transfer succeeds, the previous node is notified
   * that it should keep a forwarding reference to the node.
   * </ul>
   */
  @Override
  public void process(SerializedReadablePage page) throws Exception {
    // Find the destination node for the page
    RemoteNode initialNode = locator.find(page.getMetadata().getId().toString());
    // Send the transfer to the node
    PageTransfer transfer = new PageTransfer(locator.local(),
                                             pageReplicationFactor,
                                             page.getMetadata(),
                                             page.getInputStream());
    PageTransferResponse response = initialNode.getDFSService().offer(transfer);

    switch (response) {
      case Overloaded:
        pageQueue.add(page);
        break;

      case OutOfCapacity:
        throw new IOException("Remote node is over capacity");

      case Accepting:
        inProgressTransfers.put(transfer.getId(), page);
    }
  }

  public void notifyResult(String transferId, PageTransferResult result) {
    switch (result) {
      case ChecksumFailed:
      case Invalid:
      case OtherError:
        SerializedReadablePage page = inProgressTransfers.get(transferId);
        if (page == null) {
          throw new IllegalStateException("Transfer not currently in progress: "
                                              .concat(transferId));
        }
        // Put the page back on the queue
        pageQueue.add(page);
    }
    inProgressTransfers.remove(transferId);
  }
}
