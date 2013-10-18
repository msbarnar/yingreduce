package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public interface StreamProvider {

  OutputStream getOutputStream(String path) throws IOException;
}
