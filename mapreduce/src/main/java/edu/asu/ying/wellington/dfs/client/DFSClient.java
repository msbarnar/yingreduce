package edu.asu.ying.wellington.dfs.client;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.io.ElementOutputStream;

/**
 *
 */
public final class DFSClient {

  private final Sink<Page> pageOutSink;

  public DFSClient(Sink<Page> pageOutSink) {
    this.pageOutSink = pageOutSink;
  }

  /**
   * Returns a {@link Sink} which uploads the elements it receives to the distributed filesystem.
   */
  ElementOutputStream getElementUploadStream(TableIdentifier table) {
    return new PageBuilder(table, pageOutSink);
  }
}
