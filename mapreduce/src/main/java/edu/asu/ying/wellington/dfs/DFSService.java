package edu.asu.ying.wellington.dfs;

import java.io.IOException;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.Service;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  PageTransferResponse offer(PageTransfer transfer) throws IOException;

  boolean hasPage(PageName id);
}
