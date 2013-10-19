package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public interface StreamProvider {

  OutputStream getOutputStream(String path) throws IOException;

  InputStream getInputStream(String path) throws IOException;
}
