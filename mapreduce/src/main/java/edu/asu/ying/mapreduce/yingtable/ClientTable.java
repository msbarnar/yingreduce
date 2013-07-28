package edu.asu.ying.mapreduce.yingtable;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * {@link ClientTable} objects are the scheduling-side implementation of {@link Table}.
 */
public final class ClientTable implements Table {

  private static final long SerialVersionUID = 1L;

  // Default 200b per page
  // TODO: Set page capacity with configuration
  private static final int DEFAULT_PAGE_CAPACITY = 200;

  // Uniquely identifies the table in the data store
  private final TableID id;

  private final Deque<Page> pages = new ArrayDeque<>();


  public ClientTable(final TableID id) {
    this.id = id;
    this.addPage();
  }

  @Override
  public final TableID getId() throws RemoteException {
    return this.id;
  }

  @Override
  public final void commit() throws IOException {
    for (final Page page : this.pages) {
      this.commit(page);
    }
  }

  @Override
  public final void addElement(final Element element) throws RemoteException {
    final Page curPage = this.getCurrentPage();

    if (element.getValue().getSize() > curPage.getCapacity()) {
      throw new RemoteException(
          "Couldn't add the element to the table: the element size exceeds the maximum page"
          +" capacity.",
          new ElementTooLargeException(element));
    }

    try {
      curPage.addElement(element);
    } catch (final PageCapacityExceededException e) {
      // Commit the full page
      try {
        this.commit(curPage);
      } catch (final IOException ee) {
        throw new RemoteException("Exception while committing full page.", ee);
      }

      // Add a new page and try again
      this.addPage();
      this.addElement(element);
    }
  }

  private void addPage() {
    synchronized (this.pages) {
      // TODO: Set page capacity with configuration
      this.pages.push(new ImmutableBoundedPage(this.id, this.pages.size(), DEFAULT_PAGE_CAPACITY));
    }
  }

  private Page getCurrentPage() {
    return this.pages.peek();
  }

  private void commit(final Page page) throws IOException {
    if (page.isDirty()) {
      // TODO: push page out to the network here
      page.clean();
    }
  }
}
