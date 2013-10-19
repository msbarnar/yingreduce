package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.SerializedElement;
import edu.asu.ying.wellington.dfs.SerializedPage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Serializes an entire page to an output stream.
 */
public class PageOutputStream extends OutputStream {

  protected final OutputStream stream;

  public PageOutputStream(OutputStream stream) {
    this.stream = Preconditions.checkNotNull(stream);
  }

  /**
   * Writes the page to the underlying stream in the following sequence:
   * <p/>
   * <ol>
   * <li>The header (see: {@link PageHeader})</li>
   * <li>Serialized key->value pairs</li>
   * </ol>
   */
  public <K extends WritableComparable, V extends Writable>
  void write(SerializedPage<K, V> p) throws IOException {

    new PageHeader(p).writeTo(stream);

    for (SerializedElement<K, V> element : p) {
      write(element.getKey());
      write(element.getValue());
    }
    flush();
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
  public void close() throws IOException {
    stream.close();
  }

  @Override
  public void flush() throws IOException {
    stream.flush();
  }
}
