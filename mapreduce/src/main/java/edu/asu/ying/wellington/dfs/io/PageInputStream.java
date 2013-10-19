package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.UnboundedPage;
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
  public <K extends WritableComparable, V extends Writable>
  Page<K, V> readPage() throws IOException {

    PageHeader<?, ?> header = PageHeader.readFrom(stream);
    Class<? extends WritableComparable> keyClass = header.getKeyClass();
    Class<? extends Writable> valueClass = header.getValueClass();

    UnboundedPage<?, ?> page = new UnboundedPage<>(header.getPageID().getTableID(),
                                                   header.getPageID().getIndex(),
                                                   keyClass, valueClass);

    WritableDeserializerStream deserializer = new WritableDeserializerStream(stream);
    for (int i = 0; i < header.getNumKeys(); i++) {
      page.offer(new Element(deserializer.read(keyClass), deserializer.read(valueClass)));
    }

    return (Page<K, V>) page;
  }

  @Override
  public int read() throws IOException {
    return stream.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    return stream.read(b);
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return stream.read(b, off, len);
  }

  @Override
  public long skip(long n) throws IOException {
    return stream.skip(n);
  }

  @Override
  public int available() throws IOException {
    return stream.available();
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

  @Override
  public synchronized void mark(int readlimit) {
    stream.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    stream.reset();
  }

  @Override
  public boolean markSupported() {
    return stream.markSupported();
  }
}
