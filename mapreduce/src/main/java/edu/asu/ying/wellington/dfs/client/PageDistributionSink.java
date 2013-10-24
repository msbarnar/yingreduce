package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Inject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.SerializedReadablePage;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink
    implements Sink<SerializedReadablePage>, QueueProcessor<SerializedReadablePage> {

  private static final int DEFAULT_REPLICATION_FACTOR = 3;

  private final NodeLocator locator;

  private final int pageReplicationFactor = DEFAULT_REPLICATION_FACTOR;

  private final DelegateQueueExecutor<SerializedReadablePage> pageQueue
      = new DelegateQueueExecutor<>(this);
  private final Map<String, PageTransfer> inProgressTransfers = new HashMap<>();

  @Inject
  private PageDistributionSink(NodeLocator locator) {
    this.locator = locator;
  }

  @Override
  public void accept(final SerializedReadablePage page) throws IOException {
    if (!pageQueue.offer(page)) {
      throw new IOException("Page distribution queue is full");
    }
  }

  @Override
  public void process(SerializedReadablePage page) throws Exception {
    // Find the destination node for the page
    RemoteNode initialNode = locator.find(page.getMetadata().getId().toString());
    // Send the transfer to the node
    PageTransfer transfer = new PageTransfer(locator.local(),
                                             page.getMetadata(),
                                             page.getInputStream());
    PageTransferResponse response = initialNode.getDFSService().offer(transfer);

    switch (response) {
      case Overloaded:
        if (!pageQueue.offer(page)) {
          throw new IOException("Page distribution queue is full");
        }
        break;

      case OutOfCapacity:
        throw new IOException("Remote node is over capacity");

      case Accepting:
        inProgressTransfers.put(transfer.getId(), transfer);
    }
  }
}
