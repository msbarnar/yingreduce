package edu.asu.ying.wellington.dfs.page;

import com.google.common.collect.ImmutableMap;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.asu.ying.wellington.dfs.SerializedEntry;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code BoundedPage} is limited to a specific capacity in bytes, and will not accept entries that
 * would exceed that capacity.
 */
public final class BoundedPage implements Page {

  private static final long SerialVersionUID = 1L;

  private final TableIdentifier tableId;
  // The index of this page on the table
  private final int index;

  private final Map<WritableComparable, byte[]> contents = new LinkedHashMap<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  public BoundedPage(TableIdentifier parentTableId, int index, int capacityBytes) {

    this.tableId = parentTableId;

    this.index = index;
    this.capacityBytes = capacityBytes;
  }

  @Override
  public boolean offer(SerializedEntry entry) {
    byte[] value = entry.getValue();
    if (value.length > (capacityBytes - curSizeBytes)) {
      return false;
    }
    contents.put(entry.getKey(), value);
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
  public int offer(Iterable<SerializedEntry> entries) {
    int i = 0;
    for (SerializedEntry entry : entries) {
      if (entry.getValue().length > (capacityBytes - curSizeBytes)) {
        return i;
      }
      contents.put(entry.getKey(), entry.getValue());
      i++;
    }
    return i;
  }

  /**
   * ************************************************************ Table ID + index identify the page
   * uniquely in the database.
   */
  @Override
  public final TableIdentifier getTableId() {
    return tableId;
  }

  @Override
  public final int getIndex() {
    return index;
  }

  /**
   * **********************************************************
   */

  @Override
  public Map<WritableComparable, byte[]> getContents() {
    synchronized (contents) {
      return ImmutableMap.copyOf(contents);
    }
  }

  @Override
  public int getCapacityBytes() {
    return capacityBytes;
  }

  @Override
  public int getRemainingCapacityBytes() {
    return capacityBytes - curSizeBytes;
  }

  @Override
  public int getSizeBytes() {
    return curSizeBytes;
  }

  @Override
  public int getNumEntries() {
    return contents.size();
  }

  @Override
  public boolean isEmpty() {
    return contents.isEmpty();
  }
}
