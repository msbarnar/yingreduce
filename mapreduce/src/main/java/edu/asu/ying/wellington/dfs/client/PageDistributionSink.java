package edu.asu.ying.wellington.dfs.client;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.healthmarketscience.rmiio.RemoteOutputStreamClient;
import com.healthmarketscience.rmiio.RemoteRetry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;

/**
 * {@code PageDistributionSink} distributes accepted pages to their initial peers on the network.
 */
public final class PageDistributionSink
    implements Sink<PageData>, QueueProcessor<PageData> {

  private static final Logger log = Logger.getLogger(PageDistributionSink.class.getName());

  public static final String PROPERTY_PAGE_REPLICATION = "dfs.page.replication";

  // Finds nodes to which to distribute
  private final NodeLocator locator;

  // Set in each transfer the number of nodes that the page should be forwarded to
  private int pageReplicationFactor;

  // Queues pages to be sent
  private final DelegateQueueExecutor<PageData> pageQueue
      = new DelegateQueueExecutor<>(this);

  /**
   * Creates the distribution sink.
   *
   * @param locator           The class used for locating nodes by a search string.
   * @param replicationFactor The number of nodes on which a copy of the page will be stored.
   */
  @Inject
  private PageDistributionSink(NodeLocator locator,
                               @Named(PROPERTY_PAGE_REPLICATION) int replicationFactor) {

    if (replicationFactor < 1) {
      throw new IllegalArgumentException(PROPERTY_PAGE_REPLICATION.concat(" must be >0"));
    }

    this.locator = locator;
    this.pageReplicationFactor = replicationFactor;
  }

  /**
   * Adds a page to the queue for distribution to its initial node.
   */
  @Override
  public void accept(final PageData data) throws IOException {
    pageQueue.add(data);
  }

  @Override
  public void process(PageData data) throws IOException {
    String pageName = data.header().getPage().name().toString();
    // Find the destination node for the page
    RemoteNode initialNode = locator.find(pageName);
    String nodeName = initialNode.getName();

    // Offer the transfer to the node
    PageTransfer transfer = new PageTransfer(locator.local(), pageReplicationFactor,
                                             data.header().getPage());
    final PageTransferResponse response;
    try {
      response = initialNode.getDFSService().offer(transfer);
    } catch (RemoteException e) {
      log.log(Level.WARNING, "Exception offering transfer to remote node", e);
      return;
    }

    switch (response.status) {
      case Overloaded:
        // Requeue the transfer to try again later
        pageQueue.add(data);
        log.finest(String.format("[send] %s: %s is overloaded; requeued", pageName, nodeName));
        break;

      case OutOfCapacity:
        log.finest(String.format("[send] %s: %s is over capacity", pageName, nodeName));
        throw new IOException("Remote node is over capacity");

      case Accepting:
        ByteArrayInputStream istream = new ByteArrayInputStream(data.data());
        OutputStream ostream = RemoteOutputStreamClient.wrap(response.outputStream,
                                                             RemoteRetry.SIMPLE);
        ByteStreams.copy(istream, ostream);
        ostream.close();
        log.finest(String.format("[send] %s -> %s", pageName, nodeName));
        break;

      case Duplicate:
        log.finest(String.format("[send] %s: %s already has page", pageName, nodeName));
        break;
    }
  }
}
