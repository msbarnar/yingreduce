package edu.asu.ying.dfs.io;

import com.google.common.base.Preconditions;

import java.io.DataInput;
import java.io.IOException;

import edu.asu.ying.io.Writable;

/**
 * Reads {@link Writable} objects from a stream of serialized data.
 */
public class WritableDeserializer {

  protected DataInput in;

  public WritableDeserializer(DataInput in) {
    this.in = Preconditions.checkNotNull(in);
  }

  public Writable read() throws IOException {
    String clsName = in.readUTF();
    Class<?> cls;
    try {
      cls = Class.forName(clsName);
    } catch (ClassNotFoundException e) {
      throw new IOException("Writable class not supported: ".concat(clsName));
    }

    Writable obj;
    try {
      obj = Writable.class.cast(cls.newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Writable class could not be instantiated for deserialization: "
                                .concat(cls.getName()), e);
    }
    obj.readFields(in);
    return obj;
  }

  public <T extends Writable> T read(Class<T> cls) throws IOException {
    T obj;
    try {
      obj = cls.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Writable type could not be instantiated for deserialization: "
                                .concat(cls.getName()), e);
    }
    obj.readFields(in);
    return obj;
  }
}
