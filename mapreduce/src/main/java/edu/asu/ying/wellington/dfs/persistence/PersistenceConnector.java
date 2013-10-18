package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageOutputStream;

/**
 * A {@code PersistenceConnector} provides uniform access to an underlying persistence engine
 * be it a file, relational database, or memory cache.
 */
public interface PersistenceConnector {

  PageOutputStream getOutputStream(PageIdentifier id) throws IOException;
}
