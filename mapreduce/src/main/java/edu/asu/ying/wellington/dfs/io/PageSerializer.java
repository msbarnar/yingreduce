package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.ReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class PageSerializer implements Closeable {

  protected OutputStream ostream;
  protected ElementWriter writer;

  public PageSerializer(OutputStream stream) {
    this.ostream = stream;
    this.writer = new ElementWriter(ostream);
  }

  public <K extends WritableComparable, V extends Writable>
  void serialize(ReadablePage<K, V> page) throws IOException {
    (new PageHeader<>(page.getMetadata())).writeTo(ostream);

    for (Element<K, V> element : page) {
      writer.write(element);
    }
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }
}
