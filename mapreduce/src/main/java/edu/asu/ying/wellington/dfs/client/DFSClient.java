package edu.asu.ying.wellington.dfs.client;

import com.google.inject.Provider;

import javax.inject.Inject;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 *
 */
public final class DFSClient {

  private final Provider<PageBuilder> pageBuilderProvider;

  @Inject
  private DFSClient(Provider<PageBuilder> pageBuilderProvider) {

    this.pageBuilderProvider = pageBuilderProvider;
  }

  /**
   * Returns a {@link Sink} which uploads the elements it receives to the distributed filesystem.
   */
  // FIXME: These aren't pulled from a registry,
  // so if someone gets one and writes three pages then gets another and writes four pages, the
  // first three will be overwritten.
  @SuppressWarnings("unchecked")
  public <K extends WritableComparable, V extends Writable>
  Sink<Element<K, V>> createTable(String tableName,
                                  Class<K> keyClass, Class<V> valueClass) {

    PageBuilder<K, V> pb = (PageBuilder<K, V>) pageBuilderProvider.get();
    pb.open(tableName, keyClass, valueClass);
    return pb;
  }
}
