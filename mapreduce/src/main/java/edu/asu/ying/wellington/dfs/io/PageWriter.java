package edu.asu.ying.wellington.dfs.io;

import java.io.Closeable;
import java.io.IOException;

import edu.asu.ying.wellington.dfs.SerializedPage;

/**
 *
 */
public interface PageWriter extends Closeable {

  void write(SerializedPage p) throws IOException;
}
