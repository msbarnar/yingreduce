package edu.asu.ying.wellington.dfs.persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageInputStream;
import edu.asu.ying.wellington.dfs.io.PageOutputStreamProvider;
import edu.asu.ying.wellington.dfs.io.PageWriter;

/**
 * {@code MemoryPersistenceCache} is an in-memory cache for persisting pages.
 */
public final class MemoryPersistenceCache
    implements Persistence, PageOutputStreamProvider, Runnable {

  public static final long CACHE_LIFETIME_SECONDS = 60 * 60;  // 1 hour
  private static final long CACHE_CLEAN_FREQUENCY_SECONDS = 60;  // seconds

  private final Map<PageIdentifier, CacheRecord> cache = new HashMap<>();

  @Inject
  private MemoryPersistenceCache() {
    ScheduledExecutorService cacheCleaner = Executors.newScheduledThreadPool(1);
    // Wait until CACHE_LIFETIME_SECONDS has elapsed, then run periodically
    cacheCleaner.scheduleAtFixedRate(this, CACHE_LIFETIME_SECONDS, CACHE_CLEAN_FREQUENCY_SECONDS,
                                     TimeUnit.SECONDS);
  }

  /**
   * Runs a cleanup cycle, eliminated expired entries from the cache.
   */
  @Override
  public void run() {
    synchronized (cache) {
      for (Iterator<Map.Entry<PageIdentifier, CacheRecord>> iter = cache.entrySet().iterator();
           iter.hasNext(); ) {

        Map.Entry<PageIdentifier, CacheRecord> entry = iter.next();
        if (entry.getValue().isTimedOut()) {
          iter.remove();
        }
      }
    }
  }

  /**
   * (thread-safe) Puts a record in the cache.
   */
  private void put(PageIdentifier id, CacheRecord record) {
    if (record.get() == null || record.get().length == 0) {
      return;
    }
    synchronized (cache) {
      cache.put(id, record);
    }
  }

  @Nullable
  private byte[] get(PageIdentifier id) {
    synchronized (cache) {
      CacheRecord record = cache.get(id);
      if (record == null) {
        return null;
      }
      // Refresh the record so it stays in cache longer (LRU deletion)
      record.touch();
      return record.get();
    }
  }

  /**
   * Returns an output stream that commits its contents to the memory cache when closed.
   * <p/>
   * The stream <b>must</b> be closed for the written data to be persisted.
   */
  @Override
  public PageWriter getWriter() throws IOException {
    return new PageWriter(this);
  }

  @Override
  public OutputStream getPageOutputStream(PageIdentifier id) throws IOException {
    return new CacheOutputStream(id, this);
  }

  @Override
  public PageInputStream getInputStream(PageIdentifier id) throws IOException {
    byte[] page = get(id);
    if (page == null) {
      throw new PageNotInCacheException();
    }
    return new PageInputStream(new ByteArrayInputStream(page));
  }

  private final class CacheRecord {

    private final byte[] data;
    private final long lifetimeMs;
    private long timeout;

    CacheRecord(byte[] data, long lifetimeSeconds) {
      this.data = data;
      this.lifetimeMs = 1000 * lifetimeSeconds;
      touch();
    }

    boolean isTimedOut() {
      return System.currentTimeMillis() >= timeout;
    }

    void touch() {
      this.timeout = System.currentTimeMillis() + lifetimeMs;
    }

    byte[] get() {
      return data;
    }
  }

  /**
   * An output stream that commits the stream to the memory cache when closed.
   */
  private final class CacheOutputStream extends OutputStream {

    private final PageIdentifier id;
    private final MemoryPersistenceCache cache;
    private final OutputStream stream;

    CacheOutputStream(PageIdentifier id, MemoryPersistenceCache cache) {
      this.id = id;
      this.cache = cache;
      stream = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      stream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      stream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
      stream.flush();
    }

    @Override
    public void close() {
      cache.put(id, new CacheRecord(((ByteArrayOutputStream) stream).toByteArray(),
                                    MemoryPersistenceCache.CACHE_LIFETIME_SECONDS));
    }
  }

  public final class PageNotInCacheException extends IOException {

  }
}
