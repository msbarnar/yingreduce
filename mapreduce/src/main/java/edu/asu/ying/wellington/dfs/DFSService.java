package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface DFSService extends Service {

  Table getTable(TableIdentifier id) throws TableNotFoundException;

  Sink<Page> getPageDepository();
}
