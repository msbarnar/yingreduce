package edu.asu.ying.wellington.dfs.client;

import javax.inject.Inject;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.HasPageMetadata;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class DFSClient {

  private final Sink<HasPageMetadata> pageOutSink;

  @Inject
  private DFSClient(@PageDistributor Sink<HasPageMetadata> pageOutSink) {
    this.pageOutSink = pageOutSink;
  }

  /**
   * Returns a {@link Sink} which uploads the elements it receives to the distributed filesystem.
   */
  // FIXME: These aren't pulled from a registry,
  // so if someone gets one and writes three pages then gets another and writes four pages, the
  // first three will be overwritten/
  public <K extends WritableComparable, V extends Writable>
  Sink<Element<K, V>> getElementUploadStream(String tableName,
                                             Class<K> keyClass, Class<V> valueClass) {

    return new PageBuilder<>(tableName, pageOutSink, keyClass, valueClass);
  }
}
