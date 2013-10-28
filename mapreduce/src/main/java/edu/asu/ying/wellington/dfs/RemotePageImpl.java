package edu.asu.ying.wellington.dfs;

import java.io.InputStream;

/**
 *
 */
public final class RemotePageImpl implements RemotePage {

  private final Page metadata;
  private final InputStream contents;

  public RemotePageImpl(Page metadata, InputStream contents) {
    this.metadata = metadata;
    this.contents = contents;
  }

  @Override
  public Page metadata() {
    return metadata;
  }

  @Override
  public InputStream contents() {
    return contents;
  }
}
