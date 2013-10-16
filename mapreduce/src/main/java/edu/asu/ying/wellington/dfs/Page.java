package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;

/**
 */
public interface Page extends Sink<Element> {

  /**
   * Gets the ID of the page, including its parent table and index on that table.
   */
  PageIdentifier getPageID();

  /**
   * Returns the number of entries in the page.
   */
  int getNumKeys();
}
