package edu.asu.ying.wellington.dfs;

import edu.asu.ying.wellington.dfs.table.Table;
import edu.asu.ying.wellington.dfs.table.TableIdentifier;
import edu.asu.ying.wellington.dfs.table.TableNotFoundException;

/**
 *
 */
public interface DFSService {

  Table getTable(TableIdentifier id) throws TableNotFoundException;
}
