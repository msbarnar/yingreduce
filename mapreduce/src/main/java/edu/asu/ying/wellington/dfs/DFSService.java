package edu.asu.ying.wellington.dfs;

import com.healthmarketscience.rmiio.RemoteInputStream;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.common.remoting.Exported;
import edu.asu.ying.wellington.Service;
import edu.asu.ying.wellington.dfs.io.PageHeader;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
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

  /**
   * Returns a proxy to a page stored on a remote node. The {@link RemotePage} wraps the file's
   * metadata as well as an {@link InputStream} from which the page's contents can be read.
   */
  RemotePage fetchRemotePage(PageName name) throws IOException;

  /**
   * Wraps a local page in a {@link RemoteInputStream} for remote consumption.
   * </p>
   * {@link DFSService#fetchRemotePage(PageName)} consumes this input stream.
   */
  RemoteInputStream provideRemoteInputStream(PageName name) throws IOException;

  PageHeader getPageHeader(PageName name) throws IOException;

  Persistence getPersistence();
}
