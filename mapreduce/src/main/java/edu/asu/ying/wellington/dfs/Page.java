package edu.asu.ying.wellington.dfs;

/**
 */
public interface Page {

  /**
   * Gets the ID of the page, including its parent table and index on that table.
   */
  PageIdentifier getID();

  /**
   * Returns the number of entries in the page.
   */
  int getNumKeys();
}
