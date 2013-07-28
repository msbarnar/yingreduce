package edu.asu.ying.mapreduce.yingtable;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * The {@link Table} is a key->value map, the contents of which are distributed about the network.
 * <p> This is the heart of table reference in the network; the {@code Table} is how table and reduce
 * functions reference their target table. <p> Local {@code Table} objects are proxies to remote
 * tables. When table are put into the map locally, they are distributed about the network. <p> Each
 * table is divided into one or more pages with each page containing {@code k} maximum elements.
 * When a page is full, or the table is committed, the page is distributed to nodes chosen on
 * hashing the table ID with the page index.
 */
public interface Table extends Remote, Serializable {

  TableID getId() throws RemoteException;
  /**
   * Persists dirty pages. On a client node, this means distributing them to the network.
   * On a server node, the table may be persisted to disk or cache.
   */
  void commit() throws IOException;

  void addElement(final Element element) throws RemoteException;
}
