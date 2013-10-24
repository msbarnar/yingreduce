package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.ReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@inheritDoc}
 */
public class PageWriterImpl implements PageWriter {

  protected final PageOutputStreamProvider provider;

  @Inject
  protected PageWriterImpl(PageOutputStreamProvider provider) {
    this.provider = Preconditions.checkNotNull(provider);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <K extends WritableComparable, V extends Writable>
  void write(ReadablePage<K, V> p) throws IOException {

    OutputStream stream = provider.getStream(p.getMetadata().getId());

    new PageHeader<>(p).writeTo(stream);

    ElementWriter writer = new ElementWriter(stream);
    for (Element<K, V> element : p) {
      writer.write(element);
    }

    stream.close();
  }
}
