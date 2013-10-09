package edu.asu.ying.mapreduce.database.table;

import java.io.IOException;
import java.util.Iterator;

import edu.asu.ying.mapreduce.database.Entry;

/**
 * {@code LocalReadTable} is the interface to the locally stored contents of a particular table. The
 * local contents may be incomplete if the table is distributed across the network, but the local
 * interface is ignorant of the distribution or separation of the table it references. </p> This
 * interface is used by map operations to operate on strictly locally available data.
 */
public interface LocalReadTable extends Table {

  Iterator<Entry> iterator() throws IOException;
}
