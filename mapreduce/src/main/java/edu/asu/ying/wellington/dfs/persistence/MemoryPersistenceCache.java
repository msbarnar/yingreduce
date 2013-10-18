package edu.asu.ying.wellington.dfs.persistence;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageOutputStream;

/**
 * {@code MemoryPersistenceCache} is an in-memory cache for persisting pages.
 */
public class MemoryPersistenceCache implements Persistence, PersistenceProvider, Runnable {

  public static final long CACHE_LIFETIME = 60 * 60 * 1000;  // 1 hour
  private static final long CACHE_CLEAN_FREQUENCY = 60;  // seconds

  private final Map<CacheRecord, byte[]> cache = new HashMap<>();

  @Inject
  private MemoryPersistenceCache() {
    ScheduledExecutorService cacheCleaner = Executors.newScheduledThreadPool(1);
    // Wait until CACHE_LIFETIME has elapsed, then run periodically
    cacheCleaner.scheduleAtFixedRate(this, CACHE_LIFETIME, CACHE_CLEAN_FREQUENCY, TimeUnit.SECONDS);
  }

  /**
   * Runs a cleanup cycle, eliminated expired entries from the cache.
   */
  @Override
  public void run() {
    synchronized (cache) {
      for (Iterator<Map.Entry<CacheRecord, byte[]>> iter = cache.entrySet().iterator();
           iter.hasNext(); ) {

        Map.Entry<CacheRecord, byte[]> entry = iter.next();
        if (entry.getKey().isTimedOut()) {
          iter.remove();
        }
      }
    }
  }

  /**
   * (thread-safe) Puts a record in the cache.
   */
  private void put(CacheRecord record, byte[] data) {
    if (data == null || data.length == 0) {
      return;
    }
    synchronized (cache) {
      cache.put(record, data);
    }
  }

  @Override
  public PersistenceProvider getProvider() {
    return this;
  }

  /**
   * Returns an output stream that commits its contents to the memory cache when closed.
   * <p/>
   * The stream <b>must</b> be closed for the written data to be persisted.
   */
  @Override
  public PageOutputStream getOutputStream(PageIdentifier id) throws IOException {
    return new CacheOutputStream(id, this);
  }

  private final class CacheRecord {

    private final PageIdentifier page;
    private final long timeout;

    CacheRecord(PageIdentifier page, long lifetime) {
      this.page = page;
      this.timeout = System.currentTimeMillis() + lifetime;
    }

    boolean isTimedOut() {
      return System.currentTimeMillis() >= timeout;
    }
  }

  /**
   * An output stream that commits the stream to the memory cache when closed.
   */
  private final class CacheOutputStream extends PageOutputStream {

    private final PageIdentifier id;
    private final MemoryPersistenceCache cache;

    CacheOutputStream(PageIdentifier id, MemoryPersistenceCache cache) {
      super(new ByteArrayOutputStream());
      this.id = id;
      this.cache = cache;
    }

    @Override
    public void close() {
      cache.put(new CacheRecord(id, MemoryPersistenceCache.CACHE_LIFETIME),
                ((ByteArrayOutputStream) stream).toByteArray());
    }
  }
}
