package edu.asu.ying.wellington.dfs.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageData;

/**
 * {@code PageDistributionStream} buffers output up to a single page's capacity, then sends that
 * page to its responsible node.
 */
public final class PageDistributionStream extends OutputStream {

  private final Logger log = Logger.getLogger(PageDistributionStream.class.getName());

  private final Sink<PageData> pageDistributor;

  private final ByteArrayOutputStream buffer;
  private final int capacity;

  private Page currentPage;

  public PageDistributionStream(Page firstPage, Sink<PageData> pageDistributor) {

    this.pageDistributor = pageDistributor;
    this.currentPage = firstPage;
    this.capacity = firstPage.capacity();
    this.buffer = new ByteArrayOutputStream(capacity);
  }

  public int getRemainingCapacity() {
    return capacity - buffer.size();
  }

  @Override
  public void write(int b) throws IOException {
    if (buffer.size() + 1 > capacity) {
      throw new BufferFullException();
    }
    buffer.write(b);
    checkFlush();
  }

  @Override
  public void write(byte[] b) throws IOException {
    if (buffer.size() + b.length > capacity) {
      throw new BufferFullException();
    }
    buffer.write(b);
    checkFlush();
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (buffer.size() + len > capacity) {
      throw new BufferFullException();
    }
    buffer.write(b, off, len);
    checkFlush();
  }

  @Override
  public void flush() throws IOException {
    buffer.flush();
    newPage();
  }

  @Override
  public void close() throws IOException {
    buffer.flush();
    newPage();
  }

  private void checkFlush() throws IOException {
    if (buffer.size() >= capacity) {
      newPage();
    }
  }

  /**
   * Finalizes the buffer, packages its contents into a {@link PageData} packet, passes it to the
   * page distributor, and starts a new page with an empty buffer.
   */
  private void newPage() {
    // Finalize the written page and put its size in the metadata
    byte[] bytes = buffer.toByteArray();
    currentPage.setSize(bytes.length);
    // Make a PageData packet for it
    PageData data = new PageData(currentPage, bytes);
    // Start the next page and clear the buffer
    buffer.reset();
    currentPage = currentPage.next();

    try {
      pageDistributor.accept(data);
    } catch (IOException e) {
      log.log(Level.WARNING, "Exception passing finalized page to distributor", e);
    }
  }
}