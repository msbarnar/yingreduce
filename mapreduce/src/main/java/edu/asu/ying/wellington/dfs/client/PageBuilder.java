package edu.asu.ying.wellington.dfs.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.IOException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.BoundedPageSerializer;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.HasPageMetadata;
import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to
 * a
 * page {@link Sink}.
 * </p>
 * The default implementation of the page sink is the {@link PageDistributionSink}, which sends the
 * pages to their associated nodes.
 */
//FIXME: use bin packing
public final class PageBuilder<K extends WritableComparable, V extends Writable>
    implements Sink<Element<K, V>> {

  // TODO: Set page capacity with configuration
  public static final int DEFAULT_PAGE_CAPACITY_BYTES = 200;

  // Uniquely identifies the table in the data store
  private final String tableName;

  // Sinks full pages
  private final Sink<HasPageMetadata> pageSink;

  // For creating the page
  private final Class<K> keyClass;
  private final Class<V> valueClass;

  // Stores table elements not yet committed to the network.
  private BoundedPageSerializer<K, V> currentPage = null;
  private int currentPageIndex = 0;
  private final Object currentPageLock = new Object();

  public PageBuilder(String tableName, Sink<HasPageMetadata> pageSink,
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
  public boolean offer(Element<K, V> element) throws IOException {
    if (!currentPage.offer(element)) {
      newPage();
      return offer(element);
    }
    return true;
  }

  @Override
  public int offer(Iterable<Element<K, V>> elements) throws IOException {
    int i = 0;
    for (Element<K, V> element : elements) {
      if (!offer(element)) {
        break;
      }
      i++;
    }
    return i;
  }

  /**
   * Commits the current page and starts a new one.
   */
  public void flush() throws IOException {
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
          if (!pageSink.offer(currentPage)) {
            throw new IOException("PageMetadata sink rejected page");
          }
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

  private BoundedPageSerializer<K, V> createPage() {
    return new BoundedPageSerializer<>(tableName, currentPageIndex, DEFAULT_PAGE_CAPACITY_BYTES,
                                       keyClass, valueClass);
  }
}
