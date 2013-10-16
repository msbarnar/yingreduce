package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class PageInputStream implements Closeable {

  protected InputStream stream;

  public PageInputStream(InputStream stream) {
    this.stream = stream;
  }

  public Page read() throws IOException {
    PageHeader header = PageHeader.readFrom(stream);

    Page page = new UnmodifiablePage();
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
