package edu.asu.ying.wellington.dfs.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.io.Writable;

/**
 *
 */
public class WritableOutputStream extends OutputStream {

  protected final DataOutputStream stream;

  public WritableOutputStream(OutputStream stream) {
    this.stream = new DataOutputStream(stream);
  }

  public void write(Writable o) throws IOException {
    o.write(stream);
  }

  public int write(Iterable<Writable> os) {
    int i = 0;
    for (Writable o : os) {
      try {
        write(o);
        i++;
      } catch (IOException e) {
        break;
      }
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

  public DataOutputStream getStream() {
    return stream;
  }
}
