package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import com.healthmarketscience.rmiio.GZIPRemoteOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.dfs.persistence.PersistenceConnector;

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
    try (OutputStream stream = memoryPersistence.getOutputStream(transfer.page.name())) {
      // FIXME: add monitor
      return new PageTransferResponse(new GZIPRemoteOutputStream(stream));
    } catch (IOException e) {
      return new PageTransferResponse(e);
    }
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
