package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.File;

/**
 * {@code BufferedPageFetchStream} is the {@link InputStream} implementation for distributed pages.
 * </p>
 * When initialized with a file name, the stream will asynchronously fetch enough pages to fill
 * the buffer, or at least one page if the buffer size is smaller than a single page.
 * </p>
 * When the stream reads past the end of one page, it drops that page from the buffer and fetches
 * another to fill the buffer. If the stream reads past the end of the last page in the buffer,
 * the standard {@link InputStream} read operations will block until a page is available.
 */
public final class BufferedPageFetchStream extends InputStream {

  // The number of concurrent page fetches
  private static final int N_FETCH_THREADS = 5;

  // For getting individual page input streams
  private final DFSService dfsService;

  // The file we're streaming
  private final File file;

  // FIFO blocking queue; read operations pop an item off the head and read it while
  // download operation adds items to the tail
  private final BlockingDeque<byte[]> pages;
  // For every object put in here, a page will be fetched and cached
  private final BlockingQueue<Object> pageFetchQueue = new LinkedBlockingQueue<>();
  // The index of the next page to get
  private final AtomicInteger nextPageIndex = new AtomicInteger(-1);
  // The index of the last page put in the cache
  private final AtomicInteger lastPageCached = new AtomicInteger(-1);
  // Added to the queue to signal EOF
  private final byte[] eof = new byte[0];

  // Constantly keep the page cache full
  private final ExecutorService pageDownloaders = Executors.newFixedThreadPool(N_FETCH_THREADS);

  // True when there are no more data to be written
  private boolean isClosed = false;

  // Read operations read from here
  private byte[] buffer;
  // At this position
  private int pBuffer;
  // Blocking on this lock
  private final Object bufferLock = new Object();

  /**
   * Begins asynchronously filling the buffer with pages from {@code file}.
   *
   * @param file           the file from which to read.
   * @param bufferCapacity the size of the page buffer to keep filled.
   * @param dfsService     provides input stream for each individual page.
   */
  public BufferedPageFetchStream(File file, int bufferCapacity, DFSService dfsService) {
    this.dfsService = dfsService;
    this.file = file;
    // Get the first page and set the page cache size
    nextPageIndex.set(0);
    byte[] page = fetchPage(nextPageIndex.getAndIncrement());
    // Keep at least one pages and as many as possible without exceeding bufferCapacity
    int pageCacheSize = (int) Math.max(1, (double) bufferCapacity / page.length);
    pages = new LinkedBlockingDeque<>(pageCacheSize);
    // Cache the page
    pages.addLast(page);
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
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int read = 0;
    // Keep filling the buffer and reading it until we read len bytes
    while (read < len) {
      // If EOF then return what we read
      if (!fillBuffer()) {
        return read;
      } else {
        // Either read fully or read as much as is left in the buffer
        int limit = Math.min(len, buffer.length - pBuffer);
        for (int i = off; i < limit; i++) {
          b[i] = buffer[pBuffer++];
        }
      }
    }
    return read;
  }

  /**
   * Returns {@code true} if the buffer is ready for reading; else fills the buffer with a page from
   * the
   * cache, blocking until one is available.
   * </p>
   * Returns {@code false} if the end of the file is reached.
   */
  private boolean fillBuffer() {
    if (pBuffer >= buffer.length) {
      while (!isClosed) {
        try {
          buffer = pages.takeFirst();
          // Placing the eof object in the queue closes the stream
          if (buffer == eof) {
            isClosed = true;
            return false;
          } else {
            return true;
          }
        } catch (InterruptedException ignored) {
        }
      }
    }
    return false;
  }

  /**
   * Fetches the {@code index}th page for the file.
   */
  private byte[] fetchPage(int index) {

  }

  /**
   * Adds an object to the page fetch queue for every empty slot in the page cache.
   * The page fetch workers will concurrently fetch pages.
   */
  private void fillPageCache() {
    int index = nextPageIndex.getAndIncrement();

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
    pageDownloaders.shutdownNow();
    pages.clear();
    pages.offer(eof);
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
