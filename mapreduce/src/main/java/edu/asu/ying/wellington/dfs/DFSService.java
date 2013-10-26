package edu.asu.ying.wellington.dfs;

import java.io.IOException;

import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.dfs.server.PageTransferResult;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.Service;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  PageTransferResponse offerIncoming(PageTransfer transfer) throws IOException;

  void notifyPageTransferResult(String transferId, PageTransferResult result);

  boolean hasPage(PageName id);
}
