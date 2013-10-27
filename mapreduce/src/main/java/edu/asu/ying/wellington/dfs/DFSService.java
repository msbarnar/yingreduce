package edu.asu.ying.wellington.dfs;

import java.io.IOException;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.Service;
import edu.asu.ying.wellington.dfs.server.PageTransfer;
import edu.asu.ying.wellington.dfs.server.PageTransferResponse;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

/**
 *
 */
public interface DFSService extends Service, Exported<RemoteDFSService> {

  /**
   * Returns {@code true} if the local DFS service has a copy of page {@code name}.
   */
  boolean hasPage(PageName name);

  /**
   * Offers a remote node the opportunity to download a page from the offering node.
   * <p/>
   * The remote node should respond with a {@link PageTransferResponse} indicating the action taken
   * with the page.
   */
  PageTransferResponse offer(PageTransfer transfer) throws IOException;

  /**
   * Allows a recipient of a transfer to notify this node that the transfer is complete.
   * </p>
   * If {@code exception} is not null, we should requeue the page and start over.
   * If this happens too many times, we should pick a different node.
   */
  void notifyTransferResult(String transferId, @Nullable Throwable exception);

  /**
   * Returns a sink that distributes {@link PageData} sent to it.
   */
  Sink<PageData> getDistributionSink();
}
