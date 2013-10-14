package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.dfs.table.Table;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.dfs.table.TableNotFoundException;
import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface DFSService extends Service {

  Table getTable(TableIdentifier id) throws TableNotFoundException;
}
