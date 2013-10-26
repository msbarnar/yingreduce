package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 * The {@code PersistenceEngine} manages the caching and storage of pages and their metadata.
 */
public final class PersistenceEngine implements Persistence, QueueProcessor<PageIdentifier> {

  private static final Logger log = Logger.getLogger(PersistenceEngine.class.getName());

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";

  private final PersistenceConnector cache;
  private final PersistenceConnector disk;

  // A record of all the pages stored on disk
  private final Set<PageIdentifier> pageIndex = new HashSet<>();

  DelegateQueueExecutor<PageIdentifier> cacheCommitQueue = new DelegateQueueExecutor<>(this);

  @Inject
  private PersistenceEngine(@CachePersistence PersistenceConnector cache,
                            @DiskPersistence PersistenceConnector disk,
                            @Named(PROPERTY_STORE_PATH) String storePath) {
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
                                       .concat(" or trash the page store: ")
                                       .concat(storePath), e1);
      }
    }

    cacheCommitQueue.setExecutor(Executors.newSingleThreadExecutor());
  }

  @Override
  public void storePage(PageIdentifier id, InputStream stream) throws IOException {
    cache.deleteIfExists(id);
    ByteStreams.copy(stream, cache.getOutputStream(id));
    cacheCommitQueue.add(id);
  }

  @Override
  public InputStream readPage(PageIdentifier id) throws IOException {
    if (cache.exists(id)) {
      return cache.getInputStream(id);
    } else {
      ByteStreams.copy(disk.getInputStream(id), cache.getOutputStream(id));
      return cache.getInputStream(id);
    }
  }

  @Override
  public boolean hasPage(PageIdentifier id) {
    return cache.exists(id) || disk.exists(id);
  }

  /**
   * Commits the page {@code id} from memory to disk.
   */
  @Override
  public void process(PageIdentifier id) throws Exception {
    if (!cache.exists(id)) {
      return;
    }
    ByteStreams.copy(cache.getInputStream(id), disk.getOutputStream(id));
    // Add the page to the index and save
    pageIndex.add(id);
    savePageIndex();
  }

  private void savePageIndex() throws IOException {
    disk.savePageIndex(pageIndex);
  }

  private void loadPageIndex() throws IOException {
    Set<PageIdentifier> loadedIndex = disk.loadPageIndex();
    // Add the loaded entries to the index
    pageIndex.addAll(loadedIndex);
    // If the index contained new entries not in the stored file, save it now
    if (pageIndex.size() > loadedIndex.size()) {
      disk.savePageIndex(pageIndex);
    }
  }
}
