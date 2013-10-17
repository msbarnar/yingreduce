package edu.asu.ying.wellington.dfs.io;

import com.google.common.base.Preconditions;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.io.Writable;

/**
 *
 */
public class WritableDeserializerStream implements Closeable {

  protected final DataInputStream stream;

  public WritableDeserializerStream(InputStream stream) {
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
      throw new IOException("Writable type could not be instantiated for deserialization.", e);
    }
    obj.readFields(stream);
    return obj;
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }
}
