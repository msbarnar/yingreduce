package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Paths;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageOutputStream;

/**
 *
 */
public class DiskPersistenceManager implements Persistence, PersistenceProvider {

  private final StreamProvider streamProvider;
  private final HashFunction hasher = Hashing.md5();

  @Inject
  private DiskPersistenceManager(@DiskPersistence StreamProvider fileStreamProvider) {
    this.streamProvider = fileStreamProvider;
  }

  @Override
  public PersistenceProvider getProvider() {
    return this;
  }

  @Override
  public PageOutputStream getOutputStream(PageIdentifier id) throws IOException {
    return new PageOutputStream(streamProvider.getOutputStream(getPath(id)));
  }

  /**
   * Returns a normalized version of a string safe for filesystem paths.
   */
  private String makePathString(String s) {
    return hasher.hashString(s, Charsets.UTF_8).toString();
  }

  /**
   * Returns a properly formatted path for the page where the table name is the parent directory
   * and the page identifier is the file name. Both values are normalized to avoid invalid names.
   */
  private String getPath(PageIdentifier id) {
    String tableDirectory = makePathString(id.getTableID().toString());
    String pageFile = makePathString(id.toString());
    return Paths.get(tableDirectory, pageFile).toString();
  }
}
