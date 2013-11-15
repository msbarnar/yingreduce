package edu.asu.ying.wellington.dfs.io;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.File;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.RemotePage;
import edu.asu.ying.wellington.dfs.persistence.PageNotFoundException;

/**
 * {@code PageFetchingInputStream} is the {@link InputStream} implementation for distributed pages.
 * </p>
 * When initialized with a file name, the stream will asynchronously fetch enough pages to fill
 * the buffer, or at least one page if the buffer size is smaller than a single page.
 * </p>
 * When the stream reads past the end of one page, it drops that page from the buffer and fetches
 * another to fill the buffer. If the stream reads past the end of the last page in the buffer,
 * the standard {@link InputStream} read operations will block until a page is available.
 */
public final class PageFetchingInputStream extends InputStream {

  private static final Logger log = Logger.getLogger(PageFetchingInputStream.class.getName());

  // The maximum number of concurrent page fetches
  private static final int N_FETCH_THREADS = 5;

  // For getting individual page input streams
  private final DFSService dfsService;

  // The file we're streaming
  private final File file;

  // FIFO blocking queue; read operations pop an item off the head and read it while
  // download operation adds items to the tail
  private final BlockingDeque<byte[]> pages;
  // The index of the next page to fetch
  // The constructor fetches page 0
  private final AtomicInteger nextPageToFetch = new AtomicInteger(0);
  // The index of the next page to go in the cache
  // Fetching threads wait until this is equal to the index they fetched before putting it in
  // the cache, so cached pages are in order.
  private final AtomicInteger nextPageToCache = new AtomicInteger(0);
  // Added to the queue to signal EOF
  private final byte[] eof = new byte[0];
  // Equal to the index at which the EOF is reached; stops cache fetch workers from being spawned
  // This value presents a number of race conditions which result in workers fetching indices past
  // the EOF, but I don't think it's possible to avoid that without keeping track of which indices
  // are being fetched and interrupting the ones past the EOF. Fetching should fail fast anyway.
  private volatile int eofIndex = -1;

  // Constantly keep the page cache full
  private final ExecutorService pageFetchWorkers = Executors.newCachedThreadPool();

  // True when there are no more data to be written
  private volatile boolean isClosed = false;

  // Read operations read from here
  private byte[] buffer;
  // At this position
  private int pBuffer;

  /**
   * Begins asynchronously filling the buffer with pages from {@code file}.
   *
   * @param file           the file from which to read.
   * @param bufferCapacity the size of the page buffer to keep filled.
   * @param dfsService     provides input stream for each individual page.
   */
  public PageFetchingInputStream(File file, int bufferCapacity, DFSService dfsService) {
    this.dfsService = dfsService;
    this.file = file;
    // Get the first page and set the page cache size
    byte[] page;
    try {
      page = fetchPage(nextPageToFetch.getAndIncrement());
    } catch (IOException e) {
      log.log(Level.WARNING, "Opening page stream failed: unable to retrieve first page", e);
      isClosed = true;
      pages = null;
      return;
    }
    if (page == null) {
      throw new RuntimeException("Failed to fetch initial page");
    }
    // Keep at least one pages and as many as possible without exceeding bufferCapacity
    int pageCacheSize = (int) Math.max(1, (double) bufferCapacity / page.length);
    pages = new LinkedBlockingDeque<>(pageCacheSize);
    // Cache the page
    pages.addLast(page);
    nextPageToCache.getAndIncrement();
    // Fill the rest of the page cache
    fillPageCache();
  }

  @Override
  public int read() throws IOException {
    if (!fillBuffer()) {
      return -1;
    } else {
      return buffer[pBuffer++];
    }
  }

  @Override
  public int read(@Nonnull byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  @Override
  public int read(@Nonnull byte[] b, int off, int len) throws IOException {
    int read = -1;
    // Keep filling the buffer and reading it until we read len bytes
    while (off < len) {
      // If EOF then return what we read
      if (!fillBuffer()) {
        return read;
      } else {
        // Either read fully or read as much as is left in the buffer
        read = 0;
        int limit = Math.min(len, buffer.length - pBuffer);
        for (int i = 0; i < limit; i++) {
          b[off++] = buffer[pBuffer++];
        }
        read += limit;
      }
    }
    return read;
  }

  /**
   * Returns {@code true} if the buffer is ready for reading; else fills the buffer with a page
   * from
   * the
   * cache, blocking until one is available.
   * </p>
   * Returns {@code false} if the end of the file is reached.
   */
  private boolean fillBuffer() {
    if (isClosed) {
      return false;
    }
    if (buffer == null || pBuffer >= buffer.length) {
      while (!isClosed) {
        try {
          buffer = pages.takeFirst();
          pBuffer = 0;
          // Placing the eof object in the queue closes the stream
          if (buffer == eof) {
            isClosed = true;
            return false;
          } else {
            fillPageCache();
            return true;
          }
        } catch (InterruptedException ignored) {
        }
      }
    }
    return true;
  }

  /**
   * Fetches the {@code index}th page for the file.
   */
  private byte[] fetchPage(int index) throws IOException {
    RemotePage page;
    try {
      page = dfsService.fetchRemotePage(PageName.create(file.path(), index));
    } catch (RemoteException e) {
      // EOF
      if (e.getCause().getCause() instanceof PageNotFoundException) {
        return null;
      } else {
        throw e;
      }
    }
    ByteArrayOutputStream baos;
    try (InputStream istream = page.contents()) {
      baos = new ByteArrayOutputStream(page.metadata().size());
      ByteStreams.copy(page.contents(), baos);
    }
    return baos.toByteArray();
  }

  /**
   * Adds an object to the page fetch queue for every empty slot in the page cache.
   * The page fetch workers will concurrently fetch pages.
   */
  private void fillPageCache() {
    // Don't spawn workers if we've already fetched up to the EOF
    if (eofIndex > -1) {
      return;
    }
    // Spawn a thread for each missing page, but at most N_FETCH_THREADS
    for (int i = 0; i < Math.min(N_FETCH_THREADS, pages.remainingCapacity()); i++) {
      pageFetchWorkers.submit(new Runnable() {
        @Override
        public void run() {
          int index = nextPageToFetch.getAndIncrement();
          // Don't try to fetch if someone before us found the EOF
          // Race condition: another thread may find the EOF after we check this but before we fetch
          if (eofIndex > -1 && eofIndex < index) {
            return;
          }
          byte[] page = null;
          try {
            page = fetchPage(index);
          } catch (IOException e) {
            log.log(Level.WARNING, "Page stream interrupted by exception fetching page", e);
          }
          // TODO: How to signal EOF from fetchPage?
          if (page == null) {
            // Make sure we queue the EOF when it's our turn
            page = eof;
            // Let everyone know which index is EOF so nobody after us tries to carry on
            if (eofIndex == -1 || eofIndex > index) {
              eofIndex = index;
            }
          }
          // Wait until it's our turn to put a page in the cache, ensuring that the cache is ordered
          synchronized (nextPageToCache) {
            while (nextPageToCache.get() < index) {
              try {
                // Wait until someone caches a page and notifies
                nextPageToCache.wait();
              } catch (InterruptedException ignored) {
              }
              // Give up if someone below us queued the EOF already
              if (eofIndex != -1 && eofIndex < index) {
                return;
              }
            }
            // Every other fetch worker is waiting for us to add this; let them know we did
            pages.add(page);
            nextPageToCache.getAndIncrement();
            nextPageToCache.notifyAll();
          }
        }
      });
    }
  }

  @Override
  public long skip(long n) throws IOException {
    int skipped = 0;
    while (skipped < n) {
      // Count out what's smaller: n, or what's left in the buffer
      skipped += Math.min(n - skipped, buffer.length - pBuffer);
      // Consume the buffer
      pBuffer += skipped;
      // Refill it (at the cost of a slower skip, but the next read will be faster)
      fillBuffer();
    }
    return skipped;
  }

  @Override
  public int available() throws IOException {
    return buffer.length - pBuffer;
  }

  @Override
  public void close() throws IOException {
    isClosed = true;
    eofIndex = 0;
    pageFetchWorkers.shutdownNow();
    if (pages != null) {
      pages.clear();
      pages.offer(eof);
    }
    buffer = eof;
  }

  /**
   * Does nothing.
   */
  @Override
  public synchronized void mark(int readlimit) {
  }

  /**
   * Throws an {@link IOException}.
   */
  @Override
  public synchronized void reset() throws IOException {
    throw new IOException();
  }

  /**
   * Returns false.
   */
  @Override
  public boolean markSupported() {
    return false;
  }
}
