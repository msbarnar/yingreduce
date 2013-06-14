package edu.asu.ying.mapreduce.data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


/**
 * The {@link DistributedTable} is a key->value map, the contents of which are distributed about the network.
 * <p>
 * This is the heart of data reference in the network; the {@code DistributedTable} is how table and reduce functions
 * reference their target data.
 * <p>
 * Local {@code DistributedTable} objects are proxies to remote tables. When data are put into the map locally, they are
 * distributed about the network.
 * <p>
 * Each table is divided into one or more pages with each page containing {@code k} maximum elements. When a page is
 * full, or the table is committed, the page is distributed to nodes chosen by hashing the table ID with the page index.
 */
public interface DistributedTable
	extends Map<Serializable, Serializable>
{
	/**
	 * A {@link Page} is the unit of transmission of a {@link DistributedTable}.
	 * <p>
	 * When elements of a {@code DistributedTable} are distributed on the network, they are first gathered into
	 * {@code Page} objects. All pages in a table must be sequential and continuous for the {@code DistributedTable} to
	 * remain consistent.
	 */
	public interface Page
			extends Serializable
	{
		/**
		 * The index is the unique identifier of the page within the table.
		 * <p>Indices must be zero-based, sequential, and continuous for the table to remain consistent.
		 */
		public int getIndex();
		/**
		 * The page stores a slice of the main table; the elements stored in the page are only some of those from
		 * the table.
		 * @return
		 */
		public Map<Serializable, Serializable> getElements();
	}

	/**
	 * Persists dirty pages. On a client node, this means distributing them to the network. On a server node, the table
	 * may be persisted to disk or cache.
	 * @throws IOException
	 */
	public void commit() throws IOException;
}
