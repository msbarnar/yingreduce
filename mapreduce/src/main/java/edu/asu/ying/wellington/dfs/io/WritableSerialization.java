package edu.asu.ying.wellington.dfs.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.asu.ying.wellington.io.Writable;

/**
 *
 */
public final class WritableSerialization implements Serialization<Writable> {

  private static final class WritableSerializer implements Serializer<Writable> {

    private DataOutputStream stream;

    public void open(OutputStream stream) {
      if (stream instanceof DataOutputStream) {
        this.stream = (DataOutputStream) stream;
      } else {
        this.stream = new DataOutputStream(stream);
      }
    }

    public void serialize(Writable w) throws IOException {
      if (stream == null) {
        throw new IllegalStateException("Serializer is not open");
      }
      w.write(stream);
    }

    @Override
    public void close() throws IOException {
      if (stream != null) {
        stream.close();
      }
    }
  }

  private static final class WritableDeserializer implements Deserializer<Writable> {

    private DataInputStream stream;
    private Class<Writable> writableClass;

    public WritableDeserializer(Class<Writable> cls) {
      this.writableClass = cls;
    }

    public void open(InputStream stream) {
      if (stream instanceof DataInputStream) {
        this.stream = (DataInputStream) stream;
      } else {
        this.stream = new DataInputStream(stream);
      }
    }

    public Writable deserialize() throws IOException {
      return deserialize(null);
    }

    public Writable deserialize(Writable obj) throws IOException {
      if (stream == null) {
        throw new IllegalStateException("Deserializer is not open");
      }
      Writable w;
      if (obj == null) {
        try {
          w = writableClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new IOException(String.format(
              "Can't instantiate the writable type `%s` for deserialization.",
              writableClass.getName()), e);
        }
      } else {
        w = obj;
      }
      w.readFields(stream);
      return w;
    }
  }

  public Serializer<Writable> getSerializer(Class<Writable> cls) {
    return new WritableSerializer();
  }

  public Deserializer<Writable> getDeserializer(Class<Writable> cls) {
    return new WritableDeserializer(cls);
  }
}
