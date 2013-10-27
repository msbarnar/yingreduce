package edu.asu.ying.dfs.server;

import com.google.inject.Inject;

import java.io.OutputStream;
import java.util.logging.Logger;

import edu.asu.ying.dfs.PageName;
import edu.asu.ying.dfs.persistence.CachePersistence;
import edu.asu.ying.dfs.persistence.DiskPersistence;
import edu.asu.ying.dfs.persistence.Persistence;
import edu.asu.ying.dfs.persistence.PersistenceConnector;
import edu.asu.ying.dfs.server.PageTransferResponse.Status;

/**
 * The {@code PageTransferHandler} accepts offers for page transfers and returns an output stream
 * to which the remote node can write the page. The output stream is obtained from the
 * {@link Persistence} module designated by {@link DiskPersistence}.
 */
public final class PageTransferHandler {

  private static final Logger log = Logger.getLogger(PageTransferHandler.class.getName());

  private final PersistenceConnector memoryPersistence;
  private final PersistenceConnector diskPersistence;

  @Inject
  private PageTransferHandler(@CachePersistence PersistenceConnector memoryPersistence,
                              @DiskPersistence PersistenceConnector diskPersistence) {

    this.memoryPersistence = memoryPersistence;
    this.diskPersistence = diskPersistence;
  }

  /**
   * Attempts to get an {@link OutputStream} for the offered page.
   */
  public PageTransferResponse offer(PageTransfer transfer) {
    OutputStream stream = memoryPersistence.getOutputStream(transfer.page.name());
    return new PageTransferResponse(Status.OutOfCapacity);
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
