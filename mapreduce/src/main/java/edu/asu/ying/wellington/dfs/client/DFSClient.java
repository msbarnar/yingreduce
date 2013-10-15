package edu.asu.ying.wellington.dfs.client;

import javax.inject.Inject;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.io.ElementOutputStream;

/**
 *
 */
public final class DFSClient {

  private final Sink<Page> pageOutSink;

  @Inject
  private DFSClient(@PageDistributor Sink<Page> pageOutSink) {
    this.pageOutSink = pageOutSink;
  }

  /**
   * Returns a {@link Sink} which uploads the elements it receives to the distributed filesystem.
   */
  // FIXME: These aren't pulled from a registry,
  // so if someone gets one and writes three pages then gets another and writes four pages, the
  // first three will be overwritten/
  ElementOutputStream getElementUploadStream(TableIdentifier table) {
    return new PageBuilder(table, pageOutSink);
  }
}