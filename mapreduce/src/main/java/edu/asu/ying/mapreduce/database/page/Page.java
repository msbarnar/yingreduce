package edu.asu.ying.mapreduce.database.page;

import java.io.Serializable;
import java.util.Map;

import edu.asu.ying.mapreduce.database.Key;
import edu.asu.ying.mapreduce.database.Value;
import edu.asu.ying.mapreduce.database.table.TableID;

/**
 */
public interface Page extends Serializable {

  boolean offer(final Map.Entry<Key, Value> element);

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
   * Returns the current number of elements in the page.
   */
  int getSize();

  boolean isEmpty();

  /**
   * Returns the maximum number of bytes allowed before the page requests that it be committed by
   * its parent table.
   */
  int getCapacity();

  /**
   * The page stores a slice of the main table; the elements stored in the page are only some of
   * those from the table.
   */
  Map<Key, Value> getContents();
}
