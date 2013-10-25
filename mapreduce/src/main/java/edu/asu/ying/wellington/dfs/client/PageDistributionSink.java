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
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
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

  @Inject
  private PageDistributionSink(NodeLocator locator,
                               @Named(PROPERTY_PAGE_REPLICATION) int replicationFactor) {

    this.locator = locator;
    if (replicationFactor < 1) {
      throw new IllegalArgumentException(PROPERTY_PAGE_REPLICATION.concat(" must be >0"));
    }
    this.pageReplicationFactor = replicationFactor;
  }

  @Override
  public void accept(final SerializedReadablePage page) throws IOException {
    try {
      pageQueue.put(page);
    } catch (InterruptedException e) {
      throw new IOException("Page queue interrupted");
    }
  }

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
        try {
          pageQueue.put(page);
        } catch (InterruptedException e) {
          throw new IOException("Page queue interrupted");
        }
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
        try {
          pageQueue.put(page);
        } catch (InterruptedException e) {
          throw new RuntimeException("Page queue interrupted");
        }
    }
    inProgressTransfers.remove(transferId);
  }
}
