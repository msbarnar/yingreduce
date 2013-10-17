package edu.asu.ying.wellington.dfs.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.io.Writable;

/**
 *
 */
public class WritableSerializerStream extends OutputStream {

  protected final DataOutputStream stream;

  public WritableSerializerStream(OutputStream stream) {
    if (stream instanceof DataOutputStream) {
      this.stream = (DataOutputStream) stream;
    } else {
      this.stream = new DataOutputStream(stream);
    }
  }

  public void write(Writable w) throws IOException {
    w.write(stream);
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
  public void flush() throws IOException {
    stream.flush();
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
