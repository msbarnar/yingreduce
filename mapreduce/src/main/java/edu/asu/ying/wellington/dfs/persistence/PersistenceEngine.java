package edu.asu.ying.wellington.dfs.persistence;

import java.io.IOException;
import java.io.InputStream;

import edu.asu.ying.wellington.dfs.PageIdentifier;

/**
 *
 */
public final class PersistenceEngine implements Persistence {

  @Override
  public void storePage(InputStream stream) throws IOException {
  }

  @Override
  public InputStream readPage(PageIdentifier id) throws IOException {
    return null;
  }

  @Override
  public boolean hasPage(PageIdentifier id) {
    return false;
  }
}
