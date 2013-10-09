package edu.asu.ying.mapreduce.database.table;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.mapreduce.database.page.BoundedPage;
import edu.asu.ying.mapreduce.database.page.EntriesExceedPageCapacityException;
import edu.asu.ying.mapreduce.database.page.Page;
import edu.asu.ying.mapreduce.io.Writable;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to
 * an associated {@link Sink}. </p> The sink could be, for example, a distribution queue which sends
 * pages to remote peers.
 */
public final class LocalWriteTableImpl
    implements Table, Sink<Map.Entry<Writable, Writable>> {

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

  public int getPageCount() {
    return this.currentPageIndex + 1;
  }

  public int getMaxPageSize() {
    return this.maxPageSize;
  }


  /**
   * Adds the entry to the table, starting a new page if necessary.
   *
   * @return {@code true} if the entry was added, or {@code false} if the entry is too large to fit
   *         on any page.
   */
  @Override
  public boolean offer(final Map.Entry<Writable, Writable> entry) throws IOException {
    // Serialize entry value
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    entry.getValue().write(new DataOutputStream(buffer));

    final byte[] bytes = buffer.toByteArray();
    if (bytes.length > this.currentPage.getCapacityBytes()) {
      return false;
    }

    if (bytes.length > this.currentPage.getRemainingCapacityBytes()) {
      this.newPage();
    }

    return this.currentPage.offer(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), bytes));
  }

  /**
   * Adds the entries to the table, sorting by size to improve packing efficiency and creating new
   * pages when necessary until all entries are added.
   */
  @Override
  public int offer(final Iterable<Map.Entry<Writable, Writable>> entries) throws IOException {
    // Serialize and sort the entries for packing
    final List<Map.Entry<Writable, byte[]>> serializedEntries = this.serializeEntries(entries);
    this.sortEntries(serializedEntries);

    // Capture elements that won't fit on any page
    final List<Writable> oversizedEntries = new LinkedList<>();

    Iterator<Map.Entry<Writable, byte[]>> iter;
    // Remove any elements that exceed the maximum getSizeBytes
    final int pageCapacity = this.currentPage.getCapacityBytes();
    synchronized (currentPageLock) {
      iter = serializedEntries.iterator();
      while (iter.hasNext()) {
        final Map.Entry<Writable, byte[]> entry = iter.next();

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
          final Map.Entry<Writable, byte[]> entry = iter.next();
          if (this.currentPage.offer(entry)) {
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
  private List<Map.Entry<Writable, byte[]>> serializeEntries(
      final Iterable<Map.Entry<Writable, Writable>> entries) throws IOException {

    final List<Map.Entry<Writable, byte[]>> serializedEntries = Lists.newLinkedList();

    for (final Map.Entry<Writable, Writable> entry : entries) {
      final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      entry.getValue().write(new DataOutputStream(buffer));
      serializedEntries.add(new AbstractMap.SimpleImmutableEntry<>(entry.getKey(),
                                                                   buffer.toByteArray()));
    }

    return serializedEntries;
  }

  /**
   * Sorts the entries by descending value length for bin packing.
   */
  private void sortEntries(final List<Map.Entry<Writable, byte[]>> entries) {
    Collections.sort(entries, new Comparator<Map.Entry<Writable, byte[]>>() {
      @Override
      public int compare(Map.Entry<Writable, byte[]> a, Map.Entry<Writable, byte[]> b) {
        return Longs.compare(a.getValue().length, b.getValue().length);
      }
    });
    Collections.reverse(entries);
  }

  /**
   * Sends the current page to the sink and starts a new one.
   */
  private void newPage() {
    synchronized (this.currentPageLock) {
      if (this.currentPage != null) {
        try {
          // TODO: Logging
          if (!this.pageSink.offer(this.currentPage)) {
            throw new IOException("Page sink rejected page");
          }
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
