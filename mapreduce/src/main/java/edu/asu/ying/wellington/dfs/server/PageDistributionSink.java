package edu.asu.ying.wellington.dfs.server;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import com.healthmarketscience.rmiio.RemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamClient;
import com.healthmarketscience.rmiio.RemoteRetry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.PageData;

/**
 * {@code PageDistributionSink} distributes accepted pages to their initial peers on the network.
 */
public final class PageDistributionSink
    implements PageDistributor, QueueProcessor<PageData> {

  private static final Logger log = Logger.getLogger(PageDistributionSink.class.getName());

  public static final String PROPERTY_PAGE_REPLICATION = "dfs.page.replication";

  // Finds nodes to which to distribute
  private final NodeLocator locator;

  // Set in each transfer the number of nodes that the page should be forwarded to
  private final int pageReplicationFactor;

  // Queues pages to be sent
  private final DelegateQueueExecutor<PageData> pageQueue
      = new DelegateQueueExecutor<>(this, Executors.newFixedThreadPool(3));

  // Keep track of what we're transferring so if a remote node throws an exception we can
  // put the transfer back on the queue.
  private final Map<String, PageData> inProgressTransfers = new ConcurrentHashMap<>();

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

  @Override
  public void start() {
    pageQueue.start();
  }

  /**
   * Adds a page to the queue for distribution to its initial node.
   */
  @Override
  public void accept(final PageData data) throws IOException {
    pageQueue.add(data);
  }

  /**
   * (thread-safe)
   */
  @Override
  public void process(PageData data) throws IOException {
    String pageName = data.header().getPage().name().toString();

    // Find the destination node for the page
    // FIXME: Hack: or use the one from the pagedata (set by the replicator)
    RemoteNode initialNode
        = data.destination() != null ? data.destination() : locator.find(pageName);
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
        log.info(String.format("[send] %s: %s is overloaded; requeued", pageName, nodeName));
        break;

      case OutOfCapacity:
        log.info(String.format("[send] %s: %s is over capacity", pageName, nodeName));
        throw new IOException("Remote node is over capacity");

      case Accepting:
        try {
          inProgressTransfers.put(transfer.id, data);
          writeToRemote(data, response.outputStream);
        } catch (IOException e) {
          // Put the page back on the queue to try again
          pageQueue.add(data);
        }
        log.info(String.format("[send] %s -> %s", pageName, nodeName));
        break;

      case Duplicate:
        log.info(String.format("[send] %s: %s already has page", pageName, nodeName));
        break;
    }
  }

  @Override
  public void notifyResult(String transferId, @Nullable Throwable exception) {
    PageData data = inProgressTransfers.remove(transferId);
    if (data == null) {
      log.warning(
          "Received completion notification for a transfer we didn't start; it's a mystery");
      return;
    }
    if (exception != null) {
      log.log(Level.WARNING, "Transfer recipient threw an exception after receiving;"
                             + " will try again", exception);
      // Requeue the page for distribution
      pageQueue.add(data);
    }
    // Everything ok; we distributed a page!
  }

  private void writeToRemote(PageData data, RemoteOutputStream outputStream) throws IOException {
    // Wrap the data in an input stream, create a remote connection, and copy the stream
    // contents in chunks
    OutputStream ostream = null;
    try {
      ByteArrayInputStream istream = new ByteArrayInputStream(data.data());
      ostream = RemoteOutputStreamClient.wrap(outputStream, RemoteRetry.SIMPLE);
      // Trust RMIIO to buffer the transfer properly
      ByteStreams.copy(istream, ostream);
    } finally {
      if (ostream != null) {
        try {
          ostream.close();
        } catch (IOException ignored) {
        }
      }
    }
  }
}
