package edu.asu.ying.mapreduce.database.page;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

import edu.asu.ying.mapreduce.database.element.Element;
import edu.asu.ying.mapreduce.database.table.TableID;

/**
 * A {@code Page} is a collection of {@link Element}s in a table.
 */
public interface Page extends Serializable {

  boolean offer(final Element element);
  /**
   * Gets the ID of the table to which this page belongs.
   */
  TableID getTableId();

  /**
   * The index is the unique identifier of the page within the table.
   * <p>Indices must be zero-based, sequential, and continuous for the table to remain consistent.
   */
  int getIndex();

  /**
   * Returns the current number of elements in the page.
   */
  int getSize();

  /**
   * Returns the maximum number of bytes allowed before the page requests that it be committed
   * by its parent table.
   */
  int getCapacity();

  /**
   * Returns {@code true} if the page's contents have changed since the last time it was
   * committed.
   */
  boolean isDirty();
  void clean();

  /**
   * The page stores a slice of the main table; the elements stored in the page are only some of
   * those from the table.
   */
  ImmutableMap<Element.Key, Element.Value> getElements();
}
