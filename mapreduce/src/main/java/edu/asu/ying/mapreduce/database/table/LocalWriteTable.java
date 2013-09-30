package edu.asu.ying.mapreduce.database.table;

import java.io.IOException;
import java.util.Collection;

import edu.asu.ying.mapreduce.database.element.Element;

/**
 * {@code LocalWriteTable} is the interface mapping a table to all of that table's contents across
 * the network. Elements added to a table via this interface will be distributed to appropriate
 * remote peers, and elements read via this interface will come from the peer on which those data
 * reside.
 */
public interface LocalWriteTable {

  TableID getId();

  /**
   * Returns the number of full and partially filled pages in the table.
   */
  long getPageCount();

  void accept(final Collection<Element> elements) throws IOException;
}
