package edu.asu.ying.dfs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.asu.ying.dfs.io.WritableDeserializer;
import edu.asu.ying.dfs.io.WritableSerializer;
import edu.asu.ying.io.Writable;

/**
 *
 */
public final class FileProperties
    implements Writable, Iterable<Map.Entry<String, Writable>> {

  private static final long SerialVersionUID = 1L;

  private Map<String, Writable> properties = new HashMap<>();

  public FileProperties() {
  }

  public FileProperties(FileProperties properties) {
    if (properties != null) {
      putAll(properties);
    }
  }

  public void putAll(FileProperties properties) {
    this.properties.putAll(properties.properties);
  }

  public void putAll(Map<String, Writable> properties) {
    this.properties.putAll(properties);
  }

  public Serializable put(String key, Writable value) {
    return properties.put(key, value);
  }

  public Serializable get(String key) {
    return properties.get(key);
  }

  public Serializable remove(String key) {
    return properties.remove(key);
  }

  @Override
  public Iterator<Map.Entry<String, Writable>> iterator() {
    return properties.entrySet().iterator();
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    WritableDeserializer deserializer = new WritableDeserializer(in);
    int size = in.readInt();
    this.properties = new HashMap<>(size);
    for (int i = 0; i < size; i++) {
      put(in.readUTF(), deserializer.read());
    }
  }

  @Override
  public void write(DataOutput out) throws IOException {
    WritableSerializer serializer = new WritableSerializer(out);
    out.writeInt(properties.size());
    for (Map.Entry<String, Writable> entry : properties.entrySet()) {
      out.writeUTF(entry.getKey());
      serializer.serialize(entry.getValue());
    }
  }
}
