package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

/**
 * The {@code PageTransferHandler} accepts offers for page transfers and returns an output stream
 * to which the remote node can write the page. The output stream is obtained from the
 * {@link Persistence} module designated by {@link DiskPersistence}.
 */
public final class PageTransferHandler implements QueueProcessor<PageTransfer> {

  private static final Logger log = Logger.getLogger(PageTransferHandler.class.getName());

  private final Persistence memoryPersistence;
  private final Persistence diskPersistence;

  // Asynchronously fetch pages
  private final DelegateQueueExecutor<PageTransfer> pendingTransfers
      = new DelegateQueueExecutor<>(this);

  @Inject
  private PageTransferHandler(@CachePersistence Persistence memoryPersistence,
                              @DiskPersistence Persistence diskPersistence) {

    this.memoryPersistence = memoryPersistence;
    this.diskPersistence = diskPersistence;
  }

  /**
   * Queues the transfer to be downloaded.
   */
  public PageTransferResponse offer(PageTransfer transfer) {
    pendingTransfers.add(transfer);
    return PageTransferResponse.Accepting;
  }

  /**
   * Accepts the transfer into memory and, if the page is valid, commits the page to disk.
   * <p/>
   * When the page is committed, or if an error occurs, notifies the sending node of the result.
   */
  @Override
  public void process(PageTransfer transfer) {
    PageIdentifier pageId = transfer.getMetadata().getId();
    try (OutputStream ostream
             = memoryPersistence.getOutputStream(pageId)) {
      // Consume the remote stream
      try (InputStream stream = RemoteInputStreamClient.wrap(transfer.getInputStream())) {
        // Read the stream fully to memory
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = stream.read(buffer)) > 0) {
          ostream.write(buffer, 0, read);
        }
      }
    } catch (IOException e) {
      log.log(Level.WARNING, "Uncaught exception reading remote page stream or writing page data"
                             + " locally. Remote node will be notified of failure.", e);
      return;
    }

    // If the page validates, write it to disk
    PageTransferResult result = validateTransfer(transfer.getMetadata());
    if (result == PageTransferResult.Accepted) {
      result = commitToDisk(pageId);
    }
    notifyResult(transfer, result);
  }

  private void notifyResult(PageTransfer transfer, PageTransferResult result) {
    try {
      transfer.getSendingNode().getDFSService().notifyPageTransferResult(transfer.getId(), result);
    } catch (RemoteException e) {
      log.log(Level.WARNING, "Uncaught exception notifying remote node of transfer result.", e);
    }
  }

  private PageTransferResult validateTransfer(PageMetadata metadata) {
    // FIXME: Validate the header and checksum
    return PageTransferResult.Accepted;
  }

  /**
   * Reads the page from cache onto disk, leaving it in the cache for the replicator to access.
   */
  private PageTransferResult commitToDisk(PageIdentifier id) {
    try (InputStream istream = memoryPersistence.getInputStream(id)) {
      try (OutputStream ostream = diskPersistence.getOutputStream(id)) {
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = istream.read(buffer)) > 0) {
          ostream.write(buffer, 0, read);
        }
      }
    } catch (IOException e) {
      return PageTransferResult.OtherError;
    }

    return PageTransferResult.Accepted;
  }
}
