package edu.asu.ying.wellington.dfs.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.PageData;

/**
 * {@code BufferedPageOutputStream} buffers output up to a single page's capacity, then sends that
 * page to its responsible node.
 */
public final class BufferedPageOutputStream extends OutputStream {

  // 1mb
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;

  private final Sink<PageData> pageDistributor;

  private final ByteArrayOutputStream buffer;

  public BufferedPageOutputStream(Sink<PageData> pageDistributor, int capacity) {

    this.pageDistributor = pageDistributor;
    this.buffer = new ByteArrayOutputStream(capacity);
  }

  @Override
  public void write(int b) throws IOException {
    buffer.write(b);
  }

  @Override
  public void write(byte[] b) throws IOException {
    buffer.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    buffer.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    buffer.flush();
  }

  @Override
  public void close() throws IOException {
    // TODO: Flush page and close
  }
}
