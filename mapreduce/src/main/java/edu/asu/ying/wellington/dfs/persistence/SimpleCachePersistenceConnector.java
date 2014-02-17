package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.Nullable;
import javax.inject.Inject;

import edu.asu.ying.wellington.dfs.PageName;

/**
 * {@code SimpleCachePersistenceConnector} connects the persistence engine to an in-memory cache.
 */
public final class SimpleCachePersistenceConnector implements PersistenceConnector, Runnable {

  public static final int DEFAULT_CACHE_LIFETIME_SECONDS = 10;  // 10 seconds
  private static final long CACHE_CLEAN_FREQUENCY_SECONDS = 10;  // seconds

  private final HashFunction checksumFunc = Hashing.adler32();

  private final Map<PageName, CacheRecord> cache = new HashMap<>();

  private int cacheLifetimeSeconds = DEFAULT_CACHE_LIFETIME_SECONDS;

  @Inject
  private SimpleCachePersistenceConnector() {
    ScheduledExecutorService cacheCleaner = Executors.newScheduledThreadPool(1);
    // Wait until DEFAULT_CACHE_LIFETIME_SECONDS has elapsed, then run periodically
    /*cacheCleaner.scheduleAtFixedRate(this, cacheLifetimeSeconds, CACHE_CLEAN_FREQUENCY_SECONDS,
                                     TimeUnit.SECONDS);*/
  }

  /**
   * Runs a cleanup cycle, eliminated expired entries from the cache.
   */
  @Override
  public void run() {
    synchronized (cache) {
      for (Iterator<Map.Entry<PageName, CacheRecord>> iter = cache.entrySet().iterator();
           iter.hasNext(); ) {

        Map.Entry<PageName, CacheRecord> entry = iter.next();
        if (entry.getValue().isTimedOut()) {
          iter.remove();
        }
      }
    }
  }

  // FIXME: Implement page index
  @Override
  public void savePageIndex(Set<PageName> index) throws IOException {
  }

  @Override
  public Set<PageName> loadPageIndex() throws IOException {
    return null;
  }

  @Override
  public Set<PageName> rebuildPageIndex() throws IOException {
    return null;
  }

  @Override
  public boolean exists(PageName id) {
    CacheRecord record = cache.get(id);
    if (record == null) {
      return false;
    }
    record.touch();
    return true;
  }

  @Override
  public boolean deleteIfExists(PageName id) throws IOException {
    return cache.remove(id) != null;
  }

  @Override
  public boolean validate(PageName id, int checksum) throws IOException {
    byte[] bytes = cache.get(id).get();
    if (bytes == null) {
      throw new PageNotFoundException(id);
    }
    return checksumFunc.hashBytes(bytes).asInt() == checksum;
  }

  /**
   * Gets an {@link OutputStream} that writes to the memory cache.
   * <p/>
   * The stream contents will not be committed to the cache until the stream is closed.
   */
  @Override
  public OutputStream getOutputStream(PageName id) throws IOException {
    return new CacheOutputStream(id, this);
  }

  /**
   * Gets an {@link java.io.InputStream} that reads from the memory cache.
   */
  @Override
  public InputStream getInputStream(PageName id) throws PageNotFoundException {
    byte[] page = get(id);
    if (page == null) {
      throw new PageNotFoundException(id);
    }
    return new ByteArrayInputStream(page);
  }

  /**
   * Sets the number of seconds for which new cache entries will live.
   * </p>
   * Entries already in the cache will not be affected by the new value.
   */
  void setCacheLifetime(int seconds) {
    this.cacheLifetimeSeconds = seconds;
  }

  /**
   * (thread-safe) Puts a record in the cache.
   */
  private void put(PageName id, CacheRecord record) {
    byte[] data = record.get();
    if (data == null || data.length == 0) {
      return;
    }
    synchronized (cache) {
      cache.put(id, record);
    }
  }

  @Nullable
  private byte[] get(PageName id) {
    synchronized (cache) {
      CacheRecord record = cache.get(id);
      if (record == null) {
        return null;
      }
      // Refresh the record so it stays in cache longer (LRU deletion)
      return record.get();
    }
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

    /**
     * Returns {@code true} if the record has been cached for longer than its lifetime.
     */
    boolean isTimedOut() {
      return System.currentTimeMillis() >= timeout;
    }

    /**
     * Refreshes the cache record, extending its lifetime.
     */
    void touch() {
      this.timeout = System.currentTimeMillis() + lifetimeMs;
    }

    /**
     * Refreshes the cache record and returns the cached data.
     */
    byte[] get() {
      touch();
      return data;
    }
  }

  /**
   * An output stream that commits the stream to the memory cache when closed.
   */
  private final class CacheOutputStream extends OutputStream {

    private final PageName id;
    private final SimpleCachePersistenceConnector cache;
    private final OutputStream stream;

    CacheOutputStream(PageName id, SimpleCachePersistenceConnector cache) {
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
                                    SimpleCachePersistenceConnector.this.cacheLifetimeSeconds));
    }
  }
}
