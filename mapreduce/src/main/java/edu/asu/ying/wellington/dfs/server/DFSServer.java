package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.server.ExportException;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
/* The API for this is terrible. Transfers should be neatly contained in a transaction with
 * all references needed to complete it. Instead, the remote node has to offer through the
 * DFSService and then come back and notify through here again--even though we have nothing to do
 * with transfers besides knowing who does them.
 *
 * The current exportation model is too cumbersome to justify exporting more classes to provide a
 * "transfer service", but if we implement the architectural changes for v2 then we can much more
 * effectively isolate roles.
 */
public final class DFSServer implements DFSService {

  private final RemoteDFSService proxy;

  private final PageDistributor pageDistributor;
  // Accepts pages from remote nodes and stores them before passing them to the replicator.
  private final PageTransferHandler pageTransferHandler;

  @Inject
  private DFSServer(DFSServiceExporter exporter,
                    PageDistributor pageDistributor,
                    PageTransferHandler pageTransferHandler) {
    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    this.pageDistributor = pageDistributor;
    this.pageTransferHandler = pageTransferHandler;

    pageDistributor.start();
  }

  @Override
  public void start() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PageTransferResponse offer(PageTransfer transfer) throws IOException {
    return pageTransferHandler.offer(transfer);
  }

  /**
   * {@inheritDoc}
   */
  // This call chain: Caller -> DFSServiceExporter -> DFSServer -> PageDistributionSink ...
  // ridiculous. Returning the result should be a part of the transfer transaction.
  @Override
  public void notifyTransferResult(String transferId, @Nullable Throwable exception) {
    pageDistributor.notifyResult(transferId, exception);
  }

  @Override
  public Sink<PageData> getDistributionSink() {
    return pageDistributor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasPage(PageName name) {
    return false;
  }

  @Override
  public RemoteDFSService asRemote() {
    return proxy;
  }
}
