package edu.asu.ying.mapreduce.database.table;

import java.io.IOException;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.database.element.Element;

/**
 * {@code LocalReadTable} is the interface to the locally stored contents of a particular table. The
 * local contents may be incomplete if the table is distributed across the network, but the local
 * interface is ignorant of the distribution or separation of the table it references.
 * </p>
 * This interface is used by map operations to operate on strictly locally available data.
 */
public interface LocalReadTable {

  TableID getId();

  /**
   * Returns the next element in the table, or {@code null} if no elements are remaining.
   */
  @Nullable
  Element getNextElement() throws IOException;

  /**
   * Returns either the next {@code count} elements from the table, or all of the remaining elements
   * if fewer than {@code count}. Returns an empty collection if no elements are remaining.
   * @param count the number of elements to return.
   */
  Iterable<Element> getNextElements(final int count) throws IOException;
}
