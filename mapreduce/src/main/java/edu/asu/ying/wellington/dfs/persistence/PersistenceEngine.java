package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
  private static final String PAGE_INDEX_NAME = "pages.idx";

  private final PersistenceConnector cache;
  private final PersistenceConnector disk;

  // A record of all the pages stored on disk
  private final Set<PageIdentifier> pageIndex = new HashSet<>();
  private final File pageIndexFile;
  private final File pageIndexFileBak;

  DelegateQueueExecutor<PageIdentifier> cacheCommitQueue = new DelegateQueueExecutor<>(this);

  @Inject
  private PersistenceEngine(@CachePersistence PersistenceConnector cache,
                            @DiskPersistence PersistenceConnector disk,
                            @Named(PROPERTY_STORE_PATH) String storePath) {
    this.cache = cache;
    this.disk = disk;

    this.pageIndexFile = new File(Paths.get(storePath, PAGE_INDEX_NAME).toUri());
    this.pageIndexFileBak = new File(Paths.get(storePath, "~" + PAGE_INDEX_NAME).toUri());

    try {
      loadPageIndex();
    } catch (IOException e) {
      log.log(Level.WARNING, "Exception reading page index; reindexing", e);
      try {
        pageIndex.clear();
        pageIndex.addAll(disk.getAllStoredPages());
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

  /**
   * Serializes the page index to a file.
   */
  private void savePageIndex() throws IOException {
    // Don't allow saving and loading to interleave
    synchronized (pageIndexFile) {
      // Back up the index
      Files.copy(pageIndexFile.toPath(), pageIndexFileBak.toPath(),
                 StandardCopyOption.REPLACE_EXISTING);

      // Delete existing
      Files.deleteIfExists(pageIndexFile.toPath());
      // Write the index
      try (DataOutputStream ostream
               = new DataOutputStream(
          new BufferedOutputStream(new FileOutputStream(pageIndexFile)))) {
        // Don't allow the index to be modified while saving
        synchronized (pageIndex) {
          // Start with the number of entries in the index
          ostream.writeInt(pageIndex.size());
          for (PageIdentifier id : pageIndex) {
            id.write(ostream);
          }
        }
      } catch (Exception e) {
        // Copy the backup back to the regular file
        Files.copy(pageIndexFileBak.toPath(), pageIndexFile.toPath(),
                   StandardCopyOption.REPLACE_EXISTING);
        log.log(Level.WARNING, "Exception saving page index", e);
      }
    }
  }

  /**
   * Deserializes the page index from a file.
   */
  private void loadPageIndex() throws IOException {
    Set<PageIdentifier> loadedIndex = new HashSet<>();
    // Don't allow saving and loading to interleave
    synchronized (pageIndexFile) {
      try (DataInputStream istream
               = new DataInputStream(new BufferedInputStream(new FileInputStream(pageIndexFile)))) {
        // Read the number of entries from the file
        for (int i = 0; i < istream.readInt(); i++) {
          loadedIndex.add(PageIdentifier.readFrom(istream));
        }
      }
    }

    // Add the loaded entries to the index
    pageIndex.addAll(loadedIndex);
    // If the index contained new entries not in the stored file, save it now
    if (pageIndex.size() > loadedIndex.size()) {
      savePageIndex();
    }
  }
}
