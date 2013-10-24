package edu.asu.ying.wellington.dfs.persistence;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.io.PageInputStream;
import edu.asu.ying.wellington.dfs.io.PageOutputStreamProvider;

/**
 *
 */
public final class DiskPersistenceManager implements Persistence, PageOutputStreamProvider {

  private final StreamProvider streamProvider;
  private final HashFunction hasher = Hashing.md5();

  @Inject
  private DiskPersistenceManager(@DiskPersistence StreamProvider fileStreamProvider) {
    this.streamProvider = fileStreamProvider;
  }

  @Override
  public OutputStream getStream(PageIdentifier id) throws IOException {
    return streamProvider.getOutputStream(getPath(id));
  }

  @Override
  public OutputStream getOutputStream(PageIdentifier id) throws IOException {
    return null;
  }

  @Override
  public PageInputStream getInputStream(PageIdentifier id) throws IOException {
    return new PageInputStream(streamProvider.getInputStream(getPath(id)));
  }

  /**
   * Returns a properly formatted path for the page where the table name is the parent directory
   * and the page identifier is the file name. Both values are normalized to avoid invalid names.
   */
  private String getPath(PageIdentifier id) {
    String tableDirectory = makePathString(id.getTableName());
    String pageFile = makePathString(id.toString());
    return Paths.get(tableDirectory, pageFile).toString();
  }

  /**
   * Returns a normalized version of a string safe for filesystem paths.
   */
  private String makePathString(String s) {
    return hasher.hashString(s, Charsets.UTF_8).toString();
  }
}
