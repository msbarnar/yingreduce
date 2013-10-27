package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse.Status;

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
    return new PageTransferResponse(Status.OutOfCapacity);
  }

  @Override
  public void process(PageTransfer transfer) {
    // FIXME: Accept the transfer
  }

  /**
   * Reads the page from cache onto disk, leaving it in the cache for the replicator to access.
   */
  private PageTransferResult commitToDisk(PageName id) {
    /*try (InputStream istream = memoryPersistence.getInputStream(id)) {
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

    return PageTransferResult.Accepted;*/
    //FIXME: Commit from cache to disk
    return null;
  }
}
