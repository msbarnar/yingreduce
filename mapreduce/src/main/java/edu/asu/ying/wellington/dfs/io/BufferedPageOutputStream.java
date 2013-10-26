package edu.asu.ying.wellington.dfs.io;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.server.PageDistributor;

/**
 * {@code BufferedPageOutputStream} buffers output up to a single page's capacity, then sends that
 * page to its responsible node.
 */
public final class BufferedPageOutputStream extends OutputStream {

  @Inject
  private BufferedPageOutputStream(@PageDistributor Sink<PageData>)

  @Override
  public void write(int b) throws IOException {
  }

  @Override
  public void write(byte[] b) throws IOException {
    super.write(b);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    super.write(b, off, len);
  }

  @Override
  public void flush() throws IOException {
    super.flush();
  }

  @Override
  public void close() throws IOException {
    super.close();
  }
}
