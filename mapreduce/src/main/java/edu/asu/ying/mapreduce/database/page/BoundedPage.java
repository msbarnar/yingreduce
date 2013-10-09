package edu.asu.ying.mapreduce.database.page;

import com.google.common.collect.ImmutableMap;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.asu.ying.mapreduce.database.Key;
import edu.asu.ying.mapreduce.database.Value;
import edu.asu.ying.mapreduce.database.table.TableID;

/**
 *
 */
public final class BoundedPage implements Page {

  private static final long SerialVersionUID = 1L;

  private final TableID tableId;
  // The index of this page on the table
  private final int index;

  private final Map<Key, Value> contents = new LinkedHashMap<>();
  // Don't accept any entries that would cause the page to exceed this getSize
  private final int capacity;
  // Keep track of the total getSize of the contents of all contents
  private int curSize = 0;

  public BoundedPage(final TableID parentTableId,
                     final int index,
                     final int capacity) {

    this.tableId = parentTableId;

    this.index = index;
    this.capacity = capacity;
  }

  @Override
  public final boolean offer(final Map.Entry<Key, Value> entry) {
    synchronized (this.contents) {
      final Value value = entry.getValue();

      if ((this.curSize + value.getSize()) > this.capacity) {
        return false;
      }

      this.contents.put(entry.getKey(), value);
      this.curSize += value.getSize();
    }

    return true;
  }

  @Override
  public final TableID getTableId() {
    return this.tableId;
  }

  @Override
  public final int getCapacity() {
    return this.capacity;
  }

  @Override
  public Map<Key, Value> getContents() {
    synchronized (this.contents) {
      return ImmutableMap.copyOf(this.contents);
    }
  }

  @Override
  public final int getIndex() {
    return this.index;
  }

  @Override
  public final int getSize() {
    return this.curSize;
  }

  @Override
  public final boolean isEmpty() {
    return this.contents.isEmpty();
  }
}
