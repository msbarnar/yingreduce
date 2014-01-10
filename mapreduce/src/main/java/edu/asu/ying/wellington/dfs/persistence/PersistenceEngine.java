package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageName;

/**
 * The {@code PersistenceEngine} manages the caching and storage of pages and their metadata.
 */
public final class PersistenceEngine implements Persistence, QueueProcessor<PageName> {

  private static final Logger log = Logger.getLogger(PersistenceEngine.class.getName());

  private final PersistenceConnector cache;
  private final PersistenceConnector disk;

  // A record of all the pages stored on disk
  private final Set<PageName> pageIndex = new HashSet<>();

  DelegateQueueExecutor<PageName> cacheCommitQueue = new DelegateQueueExecutor<>(this);

  @Inject
  private PersistenceEngine(@CachePersistence PersistenceConnector cache,
                            @DiskPersistence PersistenceConnector disk) {
    this.cache = cache;
    this.disk = disk;

    try {
      loadPageIndex();
    } catch (IOException e) {
      log.log(Level.WARNING, "Exception reading page index; reindexing", e);
      try {
        pageIndex.clear();
        pageIndex.addAll(disk.rebuildPageIndex());
      } catch (IOException e1) {
        throw new RuntimeException("Exception indexing pages; either resolve the exception"
                                   + " or trash the page store", e1);
      }
    }

    cacheCommitQueue.setExecutor(Executors.newSingleThreadExecutor());
  }

  @Override
  public void storePage(PageName name, InputStream stream) throws IOException {
    cache.deleteIfExists(name);
    try (OutputStream ostream = cache.getOutputStream(name)) {
      ByteStreams.copy(stream, ostream);
    }
    cacheCommitQueue.add(name);
  }

  /**
   * Gets the page from cache, loading it from disk if necessary.
   */
  @Override
  public InputStream readPage(PageName name) throws IOException {
    if (cache.exists(name)) {
      return cache.getInputStream(name);
    } else {
      try (InputStream istream = disk.getInputStream(name)) {
        try (OutputStream ostream = cache.getOutputStream(name)) {
          ByteStreams.copy(istream, ostream);
        }
      }

      return cache.getInputStream(name);
    }
  }

  @Override
  public boolean hasPage(PageName name) {
    return cache.exists(name) || disk.exists(name);
  }

  /**
   * Commits the page {@code id} from memory to disk.
   */
  @Override
  public void process(PageName name) throws Exception {
    if (!cache.exists(name)) {
      return;
    }
    ByteStreams.copy(cache.getInputStream(name), disk.getOutputStream(name));
    // Add the page to the index and save
    pageIndex.add(name);
    savePageIndex();
  }

  private void savePageIndex() throws IOException {
    disk.savePageIndex(pageIndex);
  }

  private void loadPageIndex() throws IOException {
    Set<PageName> loadedIndex = disk.loadPageIndex();
    // Add the loaded entries to the index
    pageIndex.addAll(loadedIndex);
    // If the index contained new entries not in the stored file, save it now
    if (pageIndex.size() > loadedIndex.size()) {
      disk.savePageIndex(pageIndex);
    }
  }
}
