package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import com.healthmarketscience.rmiio.GZIPRemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.ExportException;

import javax.annotation.Nullable;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.NodeLocator;
import edu.asu.ying.wellington.RemoteNode;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageData;
import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.RemotePage;
import edu.asu.ying.wellington.dfs.RemotePageImpl;
import edu.asu.ying.wellington.dfs.io.PageHeader;
import edu.asu.ying.wellington.dfs.persistence.PageNotFoundException;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

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

  private final NodeLocator locator;

  private final Persistence persistence;

  private final PageDistributor pageDistributor;
  // Accepts pages from remote nodes and stores them before passing them to the replicator.
  private final PageTransferHandler pageTransferHandler;

  @Inject
  private DFSServer(DFSServiceExporter exporter,
                    NodeLocator locator,
                    Persistence persistence,
                    PageDistributor pageDistributor,
                    PageTransferHandler pageTransferHandler) {
    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    this.locator = locator;
    this.persistence = persistence;
    this.pageDistributor = pageDistributor;
    this.pageTransferHandler = pageTransferHandler;

    pageDistributor.start();
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
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
   * Provides a proxy to a remote page in the following way:
   * <ol>
   * <li>Locates the remote node for a page</li>
   * <li>Gets a remote input stream to the page</li>
   * <li>Reads the header into a metadata object</li>
   * <li>Returns the metadata and input stream, which contains the page's contents</li>
   * </ol>
   */
  @Override
  public RemotePage fetchRemotePage(PageName name) throws IOException {
    // FIXME: the node should forward us to one of the replicated nodes
    RemoteNode node = locator.find(name.toString());
    InputStream istream = RemoteInputStreamClient
        .wrap(node.getDFSService().getRemoteInputStream(name));
    PageHeader header = PageHeader.readFrom(new DataInputStream(istream));
    return new RemotePageImpl(header.getPage(), istream);
  }

  @Override
  public RemoteInputStream provideRemoteInputStream(PageName name) throws IOException {
    if (!hasPage(name)) {
      throw new PageNotFoundException(name);
    }
    InputStream istream = persistence.readPage(name);
    return new GZIPRemoteInputStream(istream);
  }

  @Override
  public PageHeader getPageHeader(PageName name) throws IOException {
    try (InputStream istream = persistence.readPage(name)) {
      return PageHeader.readFrom(new DataInputStream(istream));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasPage(PageName name) {
    return persistence.hasPage(name);
  }

  @Override
  public RemoteDFSService asRemote() {
    return proxy;
  }

  @Override
  public Persistence getPersistence() {
    return persistence;
  }
}
