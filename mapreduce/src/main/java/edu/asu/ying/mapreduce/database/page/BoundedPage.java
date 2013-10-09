package edu.asu.ying.mapreduce.database.page;

import com.google.common.collect.ImmutableMap;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.database.table.TableID;
import edu.asu.ying.mapreduce.io.Writable;

/**
 * {@code BoundedPage} is limited to a specific capacity in bytes, and will not accept entries that
 * would exceed that capacity.
 */
public final class BoundedPage implements Page {

  private static final long SerialVersionUID = 1L;

  private final TableID tableId;
  // The index of this page on the table
  private final int index;

  private final Map<Writable, byte[]> contents = new LinkedHashMap<>();
  // Don't accept any entries that would cause the page to exceed this size in bytes.
  private final int capacityBytes;
  // Keep track of the sum length of the contents.
  private int curSizeBytes = 0;

  public BoundedPage(final TableID parentTableId,
                     final int index,
                     final int capacityBytes) {

    this.tableId = parentTableId;

    this.index = index;
    this.capacityBytes = capacityBytes;
  }

  @Override
  public final boolean offer(final Map.Entry<Writable, byte[]> entry) {
    final byte[] value = entry.getValue();
    if (value.length > (this.capacityBytes - this.curSizeBytes)) {
      return false;
    }
    this.contents.put(entry.getKey(), value);
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
  public final int offer(final Iterable<Map.Entry<Writable, byte[]>> entries) {
    int i = 0;
    for (final Map.Entry<Writable, byte[]> entry : entries) {
      if (entry.getValue().length > (this.capacityBytes - this.curSizeBytes)) {
        return i;
      }
      this.contents.put(entry.getKey(), entry.getValue());
      i++;
    }
    return i;
  }

  /**
   * ************************************************************ Table ID + index identify the page
   * uniquely in the database.
   */
  @Override
  public final TableID getTableId() {
    return this.tableId;
  }

  @Override
  public final int getIndex() {
    return this.index;
  }

  /**
   * **********************************************************
   */

  @Override
  public Map<Writable, byte[]> getContents() {
    synchronized (this.contents) {
      return ImmutableMap.copyOf(this.contents);
    }
  }

  @Override
  public final int getCapacityBytes() {
    return this.capacityBytes;
  }

  @Override
  public final int getRemainingCapacityBytes() {
    return this.capacityBytes - this.curSizeBytes;
  }

  @Override
  public final int getSizeBytes() {
    return this.curSizeBytes;
  }

  @Override
  public int getNumEntries() {
    return this.contents.size();
  }

  @Override
  public final boolean isEmpty() {
    return this.contents.isEmpty();
  }
}
