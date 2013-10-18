package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 */
public interface Page<K extends WritableComparable, V extends Writable> {

  /**
   * Gets the ID of the page, including its parent table and index on that table.
   */
  PageIdentifier getID();

  /**
   * Returns the number of entries in the page.
   */
  int size();

  /**
   * Returns the class of keys stored in this page.
   */
  Class<K> getKeyClass();

  /**
   * Returns the class of values stored in this page.
   */
  Class<V> getValueClass();
}
