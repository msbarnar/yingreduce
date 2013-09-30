package edu.asu.ying.mapreduce.database.table;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.element.ElementSizeComparator;
import edu.asu.ying.mapreduce.database.element.ValueTooLargeException;
import edu.asu.ying.mapreduce.database.page.ImmutableBoundedPage;
import edu.asu.ying.mapreduce.database.page.Page;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to
 * an associated {@link Sink}. </p> The sink could be, for example, a distribution queue which sends
 * pages to remote peers.
 */
public final class LocalWriteTableImpl implements LocalWriteTable {

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
  public long getPageCount() {
    return this.currentPageIndex + 1;
  }

  @Override
  public void accept(final Collection<Element> elements) throws IOException {
    // Sort the elements largest-to-smallest for bin packing
    final SortedMultiset<Element> sortedElements =
        TreeMultiset.create(new ElementSizeComparator()).descendingMultiset();
    sortedElements.addAll(elements);

    // Capture elements that won't fit on any page
    final List<Element> failedElements = new ArrayList<>(elements.size());

    // Remove any elements that exceed the maximum size
    final int pageCapacity = this.currentPage.getCapacity();
    Iterator<Element> iter = sortedElements.iterator();
    while (iter.hasNext()) {
      final Element element = iter.next();
      if (element.getValue().getSize() > pageCapacity) {
        failedElements.add(element);
        iter.remove();
      } else {
        // The elements are sorted by descending size, so none of the rest can be too large
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
          final Element element = iter.next();
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
      throw new ValueTooLargeException(failedElements);
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
      this.currentPage = new ImmutableBoundedPage(this.id, this.currentPageIndex,
                                                  DEFAULT_PAGE_CAPACITY);
    }
  }
}
