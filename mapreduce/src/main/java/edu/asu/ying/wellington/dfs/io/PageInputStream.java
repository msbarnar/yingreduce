package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.SerializedPage;
import edu.asu.ying.wellington.dfs.SerializedUnboundedPage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class PageInputStream extends InputStream {

  protected final InputStream stream;

  public PageInputStream(InputStream stream) {
    this.stream = Preconditions.checkNotNull(stream);
  }

  @SuppressWarnings("unchecked")
  SerializedPage<?, ?> readPage() throws IOException {

    PageHeader<?, ?> header = PageHeader.readFrom(stream);
    Class<? extends WritableComparable> keyClass = header.getKeyClass();
    Class<? extends Writable> valueClass = header.getValueClass();

    SerializedUnboundedPage<?, ?> page
        = new SerializedUnboundedPage<>(header.getPageID().getTableID(),
                                        header.getPageID().getIndex(),
                                        keyClass, valueClass);

    WritableDeserializerStream deserializer = new WritableDeserializerStream(stream);
    for (int i = 0; i < header.getNumKeys(); i++) {
      page.offer(new Element(deserializer.read(keyClass), deserializer.read(valueClass)));
    }

    close();
    return page;
  }

  @Override
  public int read() throws IOException {
    return 0;
  }

  @Override
  public int read(byte[] b) throws IOException {
    return super.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return super.read(b, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return super.skip(n);
  }

  @Override
  public int available() throws IOException {
    return super.available();
  }

  @Override
  public void close() throws IOException {
    super.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    super.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    super.reset();
  }

  @Override
  public boolean markSupported() {
    return super.markSupported();
  }
}
