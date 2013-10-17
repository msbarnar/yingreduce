package edu.asu.ying.wellington.dfs.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.SerializedElement;
import edu.asu.ying.wellington.dfs.SerializedPage;

/**
 *
 */
public class PageOutputStream extends OutputStream {

  protected final OutputStream stream;

  public PageOutputStream(OutputStream stream) {
    this.stream = stream;
  }

  /**
   * Writes the page to the underlying stream in the following sequence: <ol> <li>The header (see:
   * {@link PageHeader})</li> <li>An index of the offset from the end of the index of the beginning
   * of each key (except the first)</li> <li>Serialized key->value pairs</li> </ol>
   */
  public void write(SerializedPage p) throws IOException {
    new PageHeader(p).writeTo(stream);
    writeIndex(p);

    for (SerializedElement element : p) {
      write(element.getKey());
      write(element.getValue());
    }
    flush();
  }

  /**
   * Writes the offset from the end of the index of each key after the first.
   */
  private void writeIndex(SerializedPage p) throws IOException {
    DataOutputStream output = new DataOutputStream(this);
    // Don't write index of first key, since that's always 0
    for (SerializedElement element : p) {
      // The
      output.writeInt(element.length);
    }
    output.flush();
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
