package edu.asu.ying.wellington.dfs.client;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.BoundedPage;
import edu.asu.ying.wellington.dfs.Element;
import edu.asu.ying.wellington.dfs.ElementsExceedPageCapacityException;
import edu.asu.ying.wellington.dfs.Page;
import edu.asu.ying.wellington.dfs.SerializedElement;
import edu.asu.ying.wellington.dfs.TableIdentifier;
import edu.asu.ying.wellington.dfs.io.ElementOutputStream;

/**
 * {@code LocalWriteTable} accepts elements locally, places them on pages, and sends full pages to a
 * page {@link Sink}. </p> The default implementation of the page sink is the {@link
 * PageDistributionSink}, which sends the pages to their associated nodes.
 */
//FIXME: use bin packing
public final class PageBuilder implements ElementOutputStream {

  // TODO: Set page capacity with configuration
  private static final int DEFAULT_PAGE_CAPACITY_BYTES = 200;

  // Uniquely identifies the table in the data store
  private final TableIdentifier id;

  // Sinks full pages
  private final Sink<Page> pageSink;

  private final int maxPageCapacityBytes = DEFAULT_PAGE_CAPACITY_BYTES;
  // Stores table elements not yet committed to the network.
  private Page currentPage = null;
  private int currentPageIndex = 0;
  private final Object currentPageLock = new Object();

  public PageBuilder(TableIdentifier id, Sink<Page> pageSink) {
    this.id = id;
    this.pageSink = pageSink;
    this.currentPage = createPage();
  }

  /**
   * Adds the element to the table, committing the current page and starting a new one if
   * necessary.
   */
  public void write(Element element) throws IOException {
    // Serialize element value
    SerializedElement serializedElement = new SerializedElement(element);

    if (serializedElement.length > maxPageCapacityBytes) {
      throw new ElementsExceedPageCapacityException(element.getKey());
    }
    if (serializedElement.length > currentPage.getRemainingCapacityBytes()) {
      newPage();
    }

    if (!currentPage.offer(serializedElement)) {
      throw new IOException("Page rejected element");
    }
  }

  /**
   * Adds the entries to the table, sorting by size to improve packing efficiency and creating new
   * pages when necessary until all entries are added.
   */
  @Override
  public int write(Iterable<Element> elements) throws IOException {
    // Serialize and sort the entries for packing
    int i = 0;
    for (SerializedElement element : serializeEntries(elements)) {
      if (element.length > maxPageCapacityBytes) {
        continue;
      }
      if (element.length > currentPage.getRemainingCapacityBytes()) {
        newPage();
      }
      if (currentPage.offer(element)) {
        i++;
      }
    }
    return i;
  }

  /**
   * Commits the current page and starts a new one.
   */
  @Override
  public void flush() throws IOException {
    newPage();
  }

  /**
   * Serializes the entries' values for sorting and storage.
   */
  private List<SerializedElement> serializeEntries(Iterable<Element> entries)
      throws IOException {

    List<SerializedElement> serializedEntries = new LinkedList<>();

    for (Element element : entries) {
      serializedEntries.add(new SerializedElement(element));
    }

    return serializedEntries;
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
            throw new IOException("Page sink rejected page");
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

  private BoundedPage createPage() {
    return new BoundedPage(id, currentPageIndex, maxPageCapacityBytes);
  }
}
