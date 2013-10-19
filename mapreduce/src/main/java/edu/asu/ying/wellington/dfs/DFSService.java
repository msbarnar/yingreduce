package edu.asu.ying.wellington.dfs;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  Table getTable(TableIdentifier id) throws TableNotFoundException;

  Sink<PageMetadata> getPageDepository();
}
