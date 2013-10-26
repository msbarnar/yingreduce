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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.inject.Inject;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 * {@code SimpleCachePersistenceConnector} connects the persistence engine to an in-memory cache.
 */
public final class SimpleCachePersistenceConnector implements PersistenceConnector, Runnable {

  public static final long CACHE_LIFETIME_SECONDS = 60 * 60;  // 1 hour
  private static final long CACHE_CLEAN_FREQUENCY_SECONDS = 60;  // seconds

  private final HashFunction checksumFunc = Hashing.adler32();

  private final Map<PageIdentifier, CacheRecord> cache = new HashMap<>();

  @Inject
  private SimpleCachePersistenceConnector() {
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

  @Override
  public boolean doesResourceExist(PageIdentifier id) {
    CacheRecord record = cache.get(id);
    if (record == null) {
      return false;
    }
    record.touch();
    return true;
  }

  @Override
  public boolean deleteIfExists(PageIdentifier id) throws IOException {
    return cache.remove(id) != null;
  }

  @Override
  public boolean validate(PageIdentifier id, int checksum) throws IOException {
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
  public OutputStream getOutputStream(PageIdentifier id) throws IOException {
    return new CacheOutputStream(id, this);
  }

  /**
   * Gets an {@link java.io.InputStream} that reads from the memory cache.
   */
  @Override
  public InputStream getInputStream(PageIdentifier id) throws PageNotFoundException {
    byte[] page = get(id);
    if (page == null) {
      throw new PageNotFoundException(id);
    }
    return new ByteArrayInputStream(page);
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

    private final PageIdentifier id;
    private final SimpleCachePersistenceConnector cache;
    private final OutputStream stream;

    CacheOutputStream(PageIdentifier id, SimpleCachePersistenceConnector cache) {
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
                                    SimpleCachePersistenceConnector.CACHE_LIFETIME_SECONDS));
    }
  }
}
