package edu.asu.ying.wellington.dfs.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.BoundedSerializedPage;
import edu.asu.ying.wellington.dfs.PageCapacityReachedException;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.dfs.SerializedReadablePage;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;
import edu.asu.ying.wellington.ybase.Element;
import edu.asu.ying.wellington.ybase.ElementTooLargeException;

/**
 * (not thread-safe) {@code LocalWriteTable} accepts elements locally, places them on pages, and
 * sends full pages to a page {@link Sink}.
 * </p>
 * The default implementation of the page sink is the {@link PageDistributionSink}, which sends the
 * pages to their associated nodes.
 */
// FIXME: use bin packing
public final class PageBuilder<K extends WritableComparable, V extends Writable>
    implements Sink<Element<K, V>>, Closeable {

  private static final Logger log = Logger.getLogger(PageBuilder.class.getName());

  public static final String PROPERTY_PAGE_CAPACITY = "dfs.page.capacity";

  // Uniquely identifies the table in the data store
  private String tableName;

  // Sinks full pages
  private final Sink<SerializedReadablePage> pageSink;
  private final int pageCapacity;

  // For creating the page
  private Class<K> keyClass;
  private Class<V> valueClass;

  // Stores table elements not yet committed to the network.
  private BoundedSerializedPage<K, V> currentPage = null;
  private int currentPageIndex = 0;
  private final Object currentPageLock = new Object();

  @Inject
  private PageBuilder(@PageDistributor Sink<SerializedReadablePage> pageOutSink,
                      @Named(PROPERTY_PAGE_CAPACITY) int pageCapacity) {

    this.pageSink = pageOutSink;
    if (pageCapacity < 1) {
      throw new IllegalArgumentException(PROPERTY_PAGE_CAPACITY.concat(" must be >0 bytes"));
    } else {
      this.pageCapacity = pageCapacity;
    }
  }

  /**
   * Creates a page builder which accepts elements with key {@code keyClass} and value
   * {@code valueClass} and places them in pages for table {@code tableName}.
   * Full pages are sent to the injected {@link Sink}.
   */
  public void open(String tableName, Class<K> keyClass, Class<V> valueClass) {

    this.tableName = Preconditions.checkNotNull(Strings.emptyToNull(tableName));
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
    if (currentPage == null) {
      throw new IllegalStateException("Page builder must open a table before it accepts elements.");
    }
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
          pageSink.accept(currentPage);
        } catch (IOException e) {
          log.log(Level.WARNING, "Unhandled exception sending page from page builder", e);
        }
        currentPageIndex++;
      }
      currentPage = createPage();
    }
  }

  private BoundedSerializedPage<K, V> createPage() {
    return new BoundedSerializedPage<>(tableName, currentPageIndex,
                                       pageCapacity,
                                       keyClass, valueClass);
  }
}
