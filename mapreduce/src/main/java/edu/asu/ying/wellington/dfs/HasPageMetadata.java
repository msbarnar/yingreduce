package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.io.Writable;
import edu.asu.ying.wellington.io.WritableComparable;

/**
 * {@code HasPageMetadata} specifies an object which provides the metadata of one and only one
 * page.
 * Each of these methods should be idempotent. A typical object implementing {@code
 * HasPageMetadata}
 * might be a stream to/from a single page or a transfer request for a single page.
 */
public interface HasPageMetadata<K extends WritableComparable, V extends Writable> {

  /**
   * Gets the ID of the page, including its parent table and index on that table.
   */
  PageIdentifier getId();

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
