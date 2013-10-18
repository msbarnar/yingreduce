package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.SerializedPage;
import edu.asu.ying.wellington.dfs.io.PageWriter;

/**
 *
 */
public final class LocalPagePersister implements PageWriter {

  private final PersistenceConnector connector;

  public LocalPagePersister(PersistenceConnector connector) {
    this.connector = connector;
  }

  @Override
  public void write(SerializedPage p) throws IOException {
  }

  @Override
  public void close() throws IOException {
  }
}
