package edu.asu.ying.dfs;

import java.io.IOException;

import edu.asu.ying.rmi.Exported;
import edu.asu.ying.dfs.server.PageTransfer;
import edu.asu.ying.dfs.server.PageTransferResponse;
import edu.asu.ying.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.service.Service;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  PageTransferResponse offer(PageTransfer transfer) throws IOException;

  boolean hasPage(PageName id);
}
