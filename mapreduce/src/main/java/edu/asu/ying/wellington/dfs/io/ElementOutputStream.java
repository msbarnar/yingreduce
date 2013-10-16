package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.Element;

/**
 *
 */
public class ElementOutputStream extends OutputStream {

  protected final WritableOutputStream stream;

  public ElementOutputStream(OutputStream stream) {
    this(new WritableOutputStream(stream));
  }

  public ElementOutputStream(WritableOutputStream stream) {
    this.stream = stream;
  }

  public void write(Element element) throws IOException {
    stream.write(element.getKey());
    stream.write(element.getValue());
  }

  public int write(Iterable<Element> elements) {
    int i = 0;
    for (Element element : elements) {
      try {
        write(element);
      } catch (IOException e) {
        return i;
      }
      i++;
    }
    return i;
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

  public WritableOutputStream getStream() {
    return stream;
  }
}
