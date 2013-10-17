package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public interface Serializer<T> extends Closeable {

  void open(OutputStream stream);

  void serialize(T obj) throws IOException;
}
