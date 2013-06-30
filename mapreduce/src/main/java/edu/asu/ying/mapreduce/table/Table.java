package edu.asu.ying.mapreduce.table;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


/**
 * The {@link Table} is a key->value map, the contents of which are distributed about the network.
 * <p> This is the heart of table reference in the network; the {@code Table} is how table and reduce
 * functions reference their target table. <p> Local {@code Table} objects are proxies to remote
 * tables. When table are put into the map locally, they are distributed about the network. <p> Each
 * table is divided into one or more pages with each page containing {@code k} maximum elements.
 * When a page is full, or the table is committed, the page is distributed to nodes chosen on
 * hashing the table ID with the page index.
 *
 * @see RemoteResource
 */
public interface Table
    extends Map<Serializable, Serializable>, RemoteResource {

  /**
   * Persists dirty pages. On a client node, this means distributing them to the network. On a scheduling
   * node, the table may be persisted to disk or cache.
   */
  void commit() throws IOException;

  /**
   * A {@link Page} is the unit of transmission of a {@link Table}. <p> When elements of a {@code
   * Table} are distributed on the network, they are first gathered into {@code Page} objects. All
   * pages in a table must be sequential and continuous for the {@code Table} to remain consistent.
   *
   * @see RemoteResource
   */
  interface Page extends Serializable, RemoteResource {

    /**
     * Every {@code Page} belongs to a {@link Table}; this is that table's ID.
     */
    String getTableId();

    /**
     * The index is the unique identifier of the page within the table. <p>Indices must be zero-based,
     * sequential, and continuous for the table to remain consistent.
     */
    int getIndex();

    /**
     * The page stores a slice of the main table; the elements stored in the page are only some of
     * those from the table.
     */
    Map<Serializable, Serializable> getElements();
  }
}
