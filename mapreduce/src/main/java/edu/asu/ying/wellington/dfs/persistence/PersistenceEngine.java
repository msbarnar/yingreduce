package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 * The {@code PersistenceEngine} manages the caching and storage of pages and their metadata.
 */
public final class PersistenceEngine implements Persistence, QueueProcessor<PageIdentifier> {

  public static final String PROPERTY_STORE_PATH = "dfs.store.path";
  private static final String PAGE_TABLE_NAME = "pages.idx";

  private final PersistenceConnector cache;
  private final PersistenceConnector disk;

  // A record of all the pages stored on disk
  private final Set<PageIdentifier> pageTable = new HashSet<>();
  private final Path pageTablePath;

  DelegateQueueExecutor<PageIdentifier> cacheCommitQueue = new DelegateQueueExecutor<>(this);

  @Inject
  private PersistenceEngine(@CachePersistence PersistenceConnector cache,
                            @DiskPersistence PersistenceConnector disk,
                            @Named(PROPERTY_STORE_PATH) String storePath) {
    this.cache = cache;
    this.disk = disk;

    this.pageTablePath = Paths.get(storePath, PAGE_TABLE_NAME);
    try {
      loadPageTable();
    } catch

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
  }

  private void savePageTable() throws IOException {
    File pageTableFile = new File()
  }

  private void loadPageTable() throws IOException {
    Set<PageIdentifier> loadedTable = new HashSet<>();

  }
}
