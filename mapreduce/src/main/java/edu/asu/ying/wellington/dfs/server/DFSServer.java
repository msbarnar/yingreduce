package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.server.ExportException;

import javax.annotation.Nullable;

import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageName;

/**
 *
 */
public final class DFSServer implements DFSService {

  private final RemoteDFSService proxy;

  // Accepts pages from remote nodes and stores them before passing them to the replicator.
  private final PageTransferHandler pageTransferHandler;

  @Inject
  private DFSServer(DFSServiceExporter exporter,
                    PageTransferHandler pageTransferHandler) {
    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    this.pageTransferHandler = pageTransferHandler;
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
  @Override
  public void notifyTransferResult(String transferId, @Nullable Throwable exception) {
    pageTransferHandler.notifyResult(transferId, exception);
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
