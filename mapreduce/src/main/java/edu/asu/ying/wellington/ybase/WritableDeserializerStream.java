package edu.asu.ying.wellington.ybase;

import com.google.common.base.Preconditions;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.io.Writable;

/**
 * Reads {@link Writable} objects from a stream of serialized data.
 */
public class WritableDeserializerStream extends InputStream {

  protected DataInputStream stream;

  public WritableDeserializerStream(InputStream stream) {
    open(stream);
  }

  public void open(InputStream stream) {
    Preconditions.checkNotNull(stream);
    if (stream instanceof DataInputStream) {
      this.stream = (DataInputStream) stream;
    } else {
      this.stream = new DataInputStream(stream);
    }
  }

  public <T extends Writable> T read(Class<T> cls) throws IOException {
    T obj;
    try {
      obj = cls.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Writable type could not be instantiated for deserialization: "
                                .concat(cls.getName()), e);
    }
    obj.readFields(stream);
    return obj;
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
