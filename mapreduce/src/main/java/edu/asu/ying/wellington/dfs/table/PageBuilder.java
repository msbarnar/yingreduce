package edu.asu.ying.wellington.dfs.table;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.Entry;
import edu.asu.ying.wellington.dfs.SerializedEntry;
import edu.asu.ying.wellington.dfs.page.BoundedPage;
import edu.asu.ying.wellington.dfs.page.EntriesExceedPageCapacityException;
import edu.asu.ying.wellington.dfs.page.Page;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to
 * an associated {@link Sink}. </p> The sink could be, for example, a distribution queue which sends
 * pages to remote peers.
 */
public final class PageBuilder implements Table, Sink<Entry> {

  private static final long SerialVersionUID = 1L;

  // TODO: Set page capacity with configuration
  private static final int DEFAULT_PAGE_CAPACITY_BYTES = 200;

  // Uniquely identifies the table in the data store
  private final TableIdentifier id;

  // Sinks full pages
  private final Sink<Page> pageSink;

  // Stores table elements not yet committed to the network.
  private Page currentPage = null;
  private int currentPageIndex = 0;
  private final int maxPageBytes = DEFAULT_PAGE_CAPACITY_BYTES;
  private final Object currentPageLock = new Object();


  public PageBuilder(final TableIdentifier id, final Sink<Page> pageSink) {
    this.id = id;
    this.pageSink = pageSink;
    this.newPage();
  }

  @Override
  public final TableIdentifier getId() {
    return id;
  }

  /**
   * Returns {@code true} if the index is the same as the current page held by the page builder. The
   * page builder does not maintain other pages, as they are immediately sent to the page sink on
   * being filled.
   */
  @Override
  public boolean hasPage(int index) {
    return index == currentPageIndex;
  }

  /**
   * Returns the number of pages this page builder has committed to the table, including the current
   * incomplete page.
   */
  public int getPageCount() {
    return currentPageIndex + 1;
  }

  /**
   * Returns the maximum number of bytes allowed in any page before that page is committed to the
   * page sink.
   */
  public int getPageCapacityBytes() {
    return maxPageBytes;
  }


  /**
   * Adds the entry to the table, starting a new page if necessary.
   *
   * @return {@code true} if the entry was added, or {@code false} if the entry is too large to fit
   *         on any page.
   */
  @Override
  public boolean offer(Entry entry) throws IOException {
    // Serialize entry value
    SerializedEntry serializedEntry = new SerializedEntry(entry);
    int length = serializedEntry.getValue().length;

    if (length > currentPage.getCapacityBytes()) {
      return false;
    }
    if (length > currentPage.getRemainingCapacityBytes()) {
      this.newPage();
    }

    return currentPage.offer(serializedEntry);
  }

  /**
   * Adds the entries to the table, sorting by size to improve packing efficiency and creating new
   * pages when necessary until all entries are added.
   */
  @Override
  public int offer(Iterable<Entry> entries) throws IOException {
    // Serialize and sort the entries for packing
    List<SerializedEntry> serializedEntries = serializeEntries(entries);
    sortEntries(serializedEntries);

    // Capture elements that won't fit on any page
    List<WritableComparable> oversizedEntries = new LinkedList<>();

    Iterator<SerializedEntry> iter;
    // Remove any elements that exceed the maximum getSizeBytes
    int pageCapacity = currentPage.getCapacityBytes();
    synchronized (currentPageLock) {
      iter = serializedEntries.iterator();
      while (iter.hasNext()) {
        SerializedEntry entry = iter.next();

        if (entry.getValue().length > pageCapacity) {
          oversizedEntries.add(entry.getKey());
          iter.remove();
        } else {
          // The elements are sorted by descending size, so none of the rest can be too large
          break;
        }
      }
    }

    int entriesAdded = 0;
    // Add all entries that fit, then start a new page and continue
    // This is O(terrible), but the packing should be better
    while (!serializedEntries.isEmpty()) {
      // Don't let anyone else fudge our list while we're iterating it
      synchronized (currentPageLock) {
        iter = serializedEntries.iterator();
        while (iter.hasNext()) {
          final SerializedEntry entry = iter.next();
          if (currentPage.offer(entry)) {
            iter.remove();
            entriesAdded++;
          }
        }
      }
      newPage();
    }

    // If there were entries too large for the page, return them in an exception.
    if (!oversizedEntries.isEmpty()) {
      throw new EntriesExceedPageCapacityException(oversizedEntries);
    }

    return entriesAdded;
  }

  /**
   * Closes the current page and starts a new one.
   */
  public void flush() {
    this.newPage();
  }

  /**
   * Serializes the entries' values for sorting and storage.
   */
  private List<SerializedEntry> serializeEntries(Iterable<Entry> entries) throws IOException {

    List<SerializedEntry> serializedEntries = Lists.newLinkedList();

    for (Entry entry : entries) {
      serializedEntries.add(new SerializedEntry(entry));
    }

    return serializedEntries;
  }

  /**
   * Sorts the entries by descending value length for bin packing.
   */
  private void sortEntries(List<SerializedEntry> entries) {
    Collections.sort(entries, new Comparator<SerializedEntry>() {
      @Override
      public int compare(SerializedEntry a,
                         SerializedEntry b) {
        return Longs.compare(a.getValue().length, b.getValue().length);
      }
    });
    Collections.reverse(entries);
  }

  /**
   * Sends the current page to the sink and starts a new one.
   */
  private void newPage() {
    synchronized (currentPageLock) {
      if (currentPage != null) {
        try {
          // TODO: Logging
          if (!pageSink.offer(currentPage)) {
            throw new IOException("Page sink rejected page");
          }
        } catch (IOException e) {
          // TODO: Logging
          e.printStackTrace();
        }
        currentPageIndex++;
      }
      // TODO: Set page capacity with configuration
      currentPage = new BoundedPage(id, currentPageIndex, maxPageBytes);
    }
  }
}
