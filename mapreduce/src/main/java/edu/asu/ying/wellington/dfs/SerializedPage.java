package edu.asu.ying.wellington.dfs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@code BoundedPage} is limited to a specific capacity in bytes, and will not accept entries that
 * would exceed that capacity.
 */
public final class SerializedPage implements Page, Iterable<SerializedElement> {

  private static final long SerialVersionUID = 1L;

  private final PageIdentifier pageId;

  private final List<SerializedElement> contents = new ArrayList<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  public SerializedPage(TableIdentifier parentTableId, int index, int capacityBytes) {

    this.pageId = PageIdentifier.create(parentTableId, index);

    this.capacityBytes = capacityBytes;
  }

  @Override
  public boolean offer(Element element) throws ElementsExceedPageCapacityException {
    // Serialize element value
    SerializedElement serializedElement = new SerializedElement(element);

    if (serializedElement.length > capacityBytes) {
      throw new ElementsExceedPageCapacityException(element.getKey());
    }
    if (serializedElement.length > getRemainingCapacityBytes()) {
      return false;
    }

    contents.add(serializedElement);
    curSizeBytes += serializedElement.length;

    return true;
  }

  /**
   * Adds as many of {@code entries} as possible without exceeding the page's capacity. </p>
   * <b>Fails fast:</b> returns on the first entry that does not fit. Entries must be sorted in
   * descending order of size prior to offering if all possible entries are to be added.
   *
   * @return the number of entries added.
   */
  @Override
  public int offer(Iterable<Element> elements) throws ElementsExceedPageCapacityException {
    int i = 0;
    for (Element element : elements) {
      if (offer(element)) {
        i++;
      }
    }
    return i;
  }

  @Override
  public PageIdentifier getPageID() {
    return pageId;
  }

  @Override
  public int getNumKeys() {
    return contents.size();
  }

  public int getCapacityBytes() {
    return capacityBytes;
  }

  public int getRemainingCapacityBytes() {
    return capacityBytes - curSizeBytes;
  }

  public int getSizeBytes() {
    return curSizeBytes;
  }

  public boolean isEmpty() {
    return contents.isEmpty();
  }

  @Override
  public Iterator<SerializedElement> iterator() {
    return null;
  }
}
