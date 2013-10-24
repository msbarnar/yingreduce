package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public class ElementWriter implements Closeable {

  protected OutputStream ostream;
  protected WritableSerializerStream serializer;

  public ElementWriter(OutputStream stream) {
    this.ostream = stream;
    this.serializer = new WritableSerializerStream(ostream);
  }

  public <K extends WritableComparable, V extends Writable> void write(Element<K, V> element)
      throws IOException {

    serializer.write(element.getKey());
    serializer.write(element.getValue());
  }

  @Override
  public void close() throws IOException {
    if (ostream != null) {
      ostream.close();
    }
  }
}
