package edu.asu.ying.mapreduce.database.table;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.mapreduce.database.Key;
import edu.asu.ying.mapreduce.database.Value;
import edu.asu.ying.mapreduce.database.ValueSizeComparator;
import edu.asu.ying.mapreduce.database.page.BoundedPage;
import edu.asu.ying.mapreduce.database.page.EntriesExceedPageCapacityException;
import edu.asu.ying.mapreduce.database.page.Page;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to
 * an associated {@link Sink}. </p> The sink could be, for example, a distribution queue which sends
 * pages to remote peers.
 */
public final class LocalWriteTableImpl implements Table, Sink<Iterable<Map.Entry<Key, Value>>> {

  private static final long SerialVersionUID = 1L;

  // Default 200b per page
  // TODO: Set page capacity with configuration
  private static final int DEFAULT_PAGE_CAPACITY = 200;

  // Uniquely identifies the table in the data store
  private final TableID id;

  // Sinks full pages
  private final Sink<Page> pageSink;

  // Stores table elements not yet committed to the network.
  private Page currentPage = null;
  private int currentPageIndex = 0;
  private final int maxPageSize = DEFAULT_PAGE_CAPACITY;
  private final Object currentPageLock = new Object();


  public LocalWriteTableImpl(final TableID id, final Sink<Page> pageSink) {
    this.id = id;
    this.pageSink = pageSink;
    this.newPage();
  }

  @Override
  public final TableID getId() {
    return this.id;
  }

  @Override
  public int getPageCount() {
    return this.currentPageIndex + 1;
  }

  @Override
  public int getMaxPageSize() {
    return this.maxPageSize;
  }

  @Override
  public void accept(final Iterable<Map.Entry<Key, Value>> entries) throws IOException {
    // Sort the elements largest-to-smallest for bin packing
    // ValueSizeComparator breaks the following collection contract:
    // (a.compareTo(b) == 0) == (a.equals(b))
    final List<Map.Entry<Key, Value>> sortedElements = Lists.newLinkedList(entries);
    Collections.sort(sortedElements, new ValueSizeComparator());
    Collections.reverse(sortedElements);

    // Capture elements that won't fit on any page
    final Map<Key, Value> failedElements = new HashMap<>();

    // Remove any elements that exceed the maximum getSize
    final int pageCapacity = this.currentPage.getCapacity();
    Iterator<Map.Entry<Key, Value>> iter = sortedElements.iterator();
    while (iter.hasNext()) {
      final Map.Entry<Key, Value> entry = iter.next();
      if (entry.getValue().getSize() > pageCapacity) {
        failedElements.put(entry.getKey(), entry.getValue());
        iter.remove();
      } else {
        // The elements are sorted by descending getSize, so none of the rest can be too large
        break;
      }
    }

    // Try to add all elements to page, from largest to smallest. This ensures the packing
    // efficiency of the first page will be high and decrease for subsequent pages.
    // Because only elements in this call to `accept` are considered, passing a higher number of
    // elements will increase packing efficiency but will take significantly longer to add.
    while (!sortedElements.isEmpty()) {
      // Don't allow the page to be modified while we're packing it
      synchronized (this.currentPageLock) {
        iter = sortedElements.iterator();
        while (iter.hasNext()) {
          final Map.Entry<Key, Value> element = iter.next();
          // If the element is added to the page, remove it from further iterations
          if (this.currentPage.offer(element)) {
            iter.remove();
          }
        }
      }
      // All the elements that would fit were added, so start again with a new page.
      this.newPage();
    }

    if (!failedElements.isEmpty()) {
      throw new EntriesExceedPageCapacityException(failedElements);
    }
  }

  /**
   * Sends the current page to the sink and starts a new one.
   */
  private void newPage() {
    synchronized (this.currentPageLock) {
      if (this.currentPage != null) {
        try {
          this.pageSink.accept(this.currentPage);
        } catch (final IOException e) {
          // TODO: Logging
          e.printStackTrace();
        }
        this.currentPageIndex++;
      }
      // TODO: Set page capacity with configuration
      this.currentPage = new BoundedPage(this.id, this.currentPageIndex, this.maxPageSize);
    }
  }
}
