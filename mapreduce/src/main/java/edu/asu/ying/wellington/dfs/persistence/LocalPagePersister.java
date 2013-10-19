package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.SerializingPage;

/**
 *
 */
public final class LocalPagePersister implements PageWriter {

  private final PersistenceProvider connector;

  public LocalPagePersister(PersistenceProvider connector) {
    this.connector = connector;
  }

  @Override
  public void write(SerializingPage p) throws IOException {
  }

  @Override
  public void close() throws IOException {
  }
}
