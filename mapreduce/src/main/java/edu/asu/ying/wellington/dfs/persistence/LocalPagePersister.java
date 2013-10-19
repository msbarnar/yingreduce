package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.SerializedPage;

/**
 *
 */
public final class LocalPagePersister implements PageWriter {

  private final PersistenceProvider connector;

  public LocalPagePersister(PersistenceProvider connector) {
    this.connector = connector;
  }

  @Override
  public void write(SerializedPage p) throws IOException {
  }

  @Override
  public void close() throws IOException {
  }
}
