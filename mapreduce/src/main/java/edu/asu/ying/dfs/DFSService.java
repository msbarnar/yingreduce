package edu.asu.ying.dfs;

import java.io.IOException;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.dfs.server.PageTransfer;
import edu.asu.ying.dfs.server.PageTransferResponse;
import edu.asu.ying.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.Service;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  PageTransferResponse offer(PageTransfer transfer) throws IOException;

  boolean hasPage(PageName id);
}
