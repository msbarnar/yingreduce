package edu.asu.ying.wellington.dfs;

import java.io.Serializable;
import java.util.List;

import edu.asu.ying.common.event.Sink;

/**
 */
public interface Page extends Serializable, Sink<SerializedElement> {

  /**
   * Gets the ID of the page, including its parent table and index on that table.
   */
  PageIdentifier getPageID();

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
  List<SerializedElement> asList();
}
