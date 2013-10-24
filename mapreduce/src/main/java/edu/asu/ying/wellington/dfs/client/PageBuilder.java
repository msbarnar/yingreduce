package edu.asu.ying.wellington.dfs.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Closeable;
import java.io.IOException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.BoundedSerializedPage;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.ElementTooLargeException;
import edu.asu.ying.wellington.dfs.PageCapacityReachedException;
import edu.asu.ying.wellington.dfs.SerializedReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * (not thread-safe) {@code LocalWriteTable} accepts elements locally, places them on pages, and
 * sends full pages to a page {@link Sink}.
 * </p>
 * The default implementation of the page sink is the {@link PageDistributionSink}, which sends the
 * pages to their associated nodes.
 */
//FIXME: use bin packing
public final class PageBuilder<K extends WritableComparable, V extends Writable>
    implements Sink<Element<K, V>>, Closeable {

  // TODO: Set page capacity with configuration
  public static final int DEFAULT_PAGE_CAPACITY_BYTES = 200;

  // Uniquely identifies the table in the data store
  private final String tableName;

  // Sinks full pages
  private final Sink<SerializedReadablePage> pageSink;

  // For creating the page
  private final Class<K> keyClass;
  private final Class<V> valueClass;

  // Stores table elements not yet committed to the network.
  private BoundedSerializedPage<K, V> currentPage = null;
  private int currentPageIndex = 0;
  private final Object currentPageLock = new Object();

  public PageBuilder(String tableName, Sink<SerializedReadablePage> pageSink,
                     Class<K> keyClass, Class<V> valueClass) {

    this.tableName = Preconditions.checkNotNull(Strings.emptyToNull(tableName));
    this.pageSink = pageSink;
    this.keyClass = keyClass;
    this.valueClass = valueClass;
    this.currentPage = createPage();
  }

  /**
   * Adds the element to the table, committing the current page and starting a new one if
   * necessary.
   */
  @Override
  public void accept(Element<K, V> element) throws IOException {
    try {
      currentPage.accept(element);
    } catch (PageCapacityReachedException e) {
      newPage();
      try {
        currentPage.accept(element);
      } catch (PageCapacityReachedException e1) {
        throw new ElementTooLargeException();
      }
    }
  }

  /**
   * Closes the current page and flushes it to the sink. The pagebuilder does not close and remains
   * usable.
   */
  @Override
  public void close() throws IOException {
    newPage();
  }

  /**
   * Sends the current page to the sink and starts a new one.
   */
  private void newPage() throws IOException {
    synchronized (currentPageLock) {
      if (currentPage != null) {
        try {
          // TODO: Logging
          pageSink.accept(currentPage);
        } catch (IOException e) {
          // TODO: Logging
          e.printStackTrace();
        }
        currentPageIndex++;
      }
      // TODO: Set page capacity with configuration
      currentPage = createPage();
    }
  }

  private BoundedSerializedPage<K, V> createPage() {
    return new BoundedSerializedPage<>(tableName, currentPageIndex,
                                       DEFAULT_PAGE_CAPACITY_BYTES,
                                       keyClass, valueClass);
  }
}
