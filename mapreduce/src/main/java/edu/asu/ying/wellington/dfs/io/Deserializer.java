package edu.asu.ying.wellington.dfs.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public interface Deserializer<T> {

  void open(InputStream stream);

  T deserialize() throws IOException;

  T deserialize(T obj) throws IOException;
}
