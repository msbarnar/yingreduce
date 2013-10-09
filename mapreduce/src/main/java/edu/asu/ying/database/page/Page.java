package edu.asu.ying.database.page;

import java.io.Serializable;
import java.util.Map;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.database.SerializedEntry;
import edu.asu.ying.database.io.WritableComparable;
import edu.asu.ying.database.table.TableID;

/**
 */
public interface Page extends Serializable, Sink<SerializedEntry> {

  /**
   * Gets the ID of the table to which this page belongs.
   */
  TableID getTableId();

  /**
   * The index is the unique identifier of the page within the table. <p>Indices must be zero-based,
   * sequential, and continuous for the table to remain consistent.
   */
  int getIndex();

  /**
   * Returns the maximum number of bytes allowed before the page is closed.
   */
  int getCapacityBytes();

  /**
   * Returns the difference between the page's capacity and its current size.
   */
  int getRemainingCapacityBytes();

  /**
   * Returns the current number of bytes in the page.
   */
  int getSizeBytes();

  /**
   * Returns the number of entries in the page.
   */
  int getNumEntries();

  /**
   * Returns true if there are zero elements in the page.
   */
  boolean isEmpty();

  /**
   * The page stores a slice of the main table; the elements stored in the page are only some of
   * those from the table.
   */
  Map<WritableComparable, byte[]> getContents();
}
