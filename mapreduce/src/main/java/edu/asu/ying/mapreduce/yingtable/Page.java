package edu.asu.ying.mapreduce.yingtable;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;

/**
 * A {@code Page} is the unit of organization of {@link Element}s in a {@link Table}.
 */
interface Page extends Serializable {

  /**
   * Adds the element to the page. If the number of elements in the page after adding exceeds the
   * page's maximum size, it will request that the parent table commit the page.
   */
  void addElement(final Element element) throws PageCapacityExceededException;

  /**
   * Every {@code Page} belongs to a {@link Table}; this is that table's ID.
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
   * </p>
   * Note that modifying the contents of the page after adding is not yet supported, as this could
   * create inconsistencies between the local representation of the page and that held by the node
   * storing the page.
   */
  ImmutableMap<Element.Key, Element.Value> getElements();
}
