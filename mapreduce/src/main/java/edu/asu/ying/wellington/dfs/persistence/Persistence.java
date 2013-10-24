package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.OutputStream;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageInputStream;

/**
 * A {@code Persistence} provides uniform access to an underlying persistence engine
 * be it a file, relational database, or memory cache.
 */
public interface Persistence {

  OutputStream getOutputStream(PageIdentifier id) throws IOException;

  PageInputStream getInputStream(PageIdentifier id) throws IOException;
}
