package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.SerializedElement;
import edu.asu.ying.wellington.dfs.SerializingPage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * Serializes an entire page to an output stream.
 */
public class PageWriter {

  protected final PageOutputStreamProvider provider;

  public PageWriter(PageOutputStreamProvider provider) {
    this.provider = Preconditions.checkNotNull(provider);
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
  void write(SerializingPage<K, V> p) throws IOException {

    OutputStream stream = provider.getPageOutputStream(p.getId());

    new PageHeader<>(p).writeTo(stream);

    for (SerializedElement<K, V> element : p) {
      stream.write(element.getKey());
      stream.write(element.getValue());
    }

    stream.close();
  }
}
