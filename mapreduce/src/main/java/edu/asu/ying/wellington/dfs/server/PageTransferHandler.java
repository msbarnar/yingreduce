package edu.asu.ying.wellington.dfs.server;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;

import com.healthmarketscience.rmiio.GZIPRemoteOutputStream;
import com.healthmarketscience.rmiio.RemoteOutputStreamServer;
import com.healthmarketscience.rmiio.RemoteStreamMonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.wellington.dfs.PageName;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.dfs.persistence.PersistenceConnector;

/**
 * The {@code PageTransferHandler} accepts offers for page transfers and returns an output stream
 * to which the remote node can write the page. The output stream is obtained from the
 * {@link Persistence} module designated by {@link DiskPersistence}.
 */
public final class PageTransferHandler {

  private static final Logger log = Logger.getLogger(PageTransferHandler.class.getName());

  private final PersistenceConnector memoryPersistence;
  private final PersistenceConnector diskPersistence;

  private final PageReplicator replicator;

  private final Map<String, PageTransfer> inProgressTransfers = new ConcurrentHashMap<>();

  @Inject
  private PageTransferHandler(@CachePersistence PersistenceConnector memoryPersistence,
                              @DiskPersistence PersistenceConnector diskPersistence,
                              PageReplicator replicator) {

    this.memoryPersistence = memoryPersistence;
    this.diskPersistence = diskPersistence;
    this.replicator = replicator;
  }

  /**
   * Attempts to get an {@link OutputStream} for the offered page.
   */
  public PageTransferResponse offer(PageTransfer transfer) {
    try (OutputStream stream = memoryPersistence.getOutputStream(transfer.page.name())) {
      // Track the transfer so the monitor can close it out
      inProgressTransfers.put(transfer.id, transfer);
      return new PageTransferResponse(
          new GZIPRemoteOutputStream(stream, new TransferMonitor(this, transfer.id)));
    } catch (IOException e) {
      return new PageTransferResponse(e);
    }
  }

  /**
   * Called by the transfer monitor when a transfer completes; retrieves the ongoing transfer
   * from the map, commits the page to disk, and notifies the sender of any exceptions.
   */
  private void closeTransfer(String transferId, Throwable e) {
    PageTransfer transfer = inProgressTransfers.remove(transferId);
    if (transfer == null) {
      // If this happens, I don't know what I'm doing and should be fired.
      log.severe("Transfer completed but was not in progress; it's a mystery");
      return;
    }

    if (e != null) {
      // This will get sent to the sending node and they'll resend
      log.log(Level.WARNING, "Exception receiving page transfer", e);
    } else {
      // Copy the transfer from cache to disk, leaving it in cache for the replicator
      try {
        commitToDisk(transfer.page.name());
      } catch (IOException x) {
        // Pass the exception along to the sending node so they can try again or pick a new node
        e = new RemoteException("Receiving node threw an uncaught exception saving the sent"
                                + " page", x);
      }
    }
    // Notify the sending node if we had any problems
    try {
      transfer.sendingNode.getDFSService().notifyTransferResult(transferId, e);
    } catch (RemoteException x) {
      log.log(Level.WARNING, "Exception notifying sending node of transfer completion", x);
    }

    // If no problems, send the page to the replicator
    if (e == null) {
      try {
        replicator.accept(transfer);
      } catch (IOException x) {
        throw new RuntimeException("Exception passing page name to replicator", x);
      }
    }
  }

  /**
   * (thread-safe) Reads the page from cache onto disk, leaving it in the cache for the replicator
   * to access.
   */
  private void commitToDisk(PageName id) throws IOException {
    try (InputStream istream = memoryPersistence.getInputStream(id)) {
      try (OutputStream ostream = diskPersistence.getOutputStream(id)) {
        ByteStreams.copy(istream, ostream);
      }
    }
  }

  private final class TransferMonitor implements RemoteStreamMonitor<RemoteOutputStreamServer> {

    private final PageTransferHandler handler;
    private final String transferId;

    private TransferMonitor(PageTransferHandler handler, String transferId) {
      this.handler = handler;
      this.transferId = transferId;
    }

    @Override
    public void failure(RemoteOutputStreamServer stream, Exception e) {
      handler.closeTransfer(transferId, e);
    }

    @Override
    public void closed(RemoteOutputStreamServer stream, boolean clean) {
      if (clean) {
        handler.closeTransfer(transferId, null);
      } else {
        handler.closeTransfer(transferId, new IOException("Stream was not closed cleanly; sending"
                                                          + " node possibly dropped while sending"));
      }
    }

    @Override
    public void bytesMoved(RemoteOutputStreamServer stream, int numBytes, boolean isReattempt) {
    }

    @Override
    public void bytesSkipped(RemoteOutputStreamServer stream, long numBytes, boolean isReattempt) {
    }

    @Override
    public void localBytesMoved(RemoteOutputStreamServer stream, int numBytes) {
    }

    @Override
    public void localBytesSkipped(RemoteOutputStreamServer stream, long numBytes) {
    }
  }
}
