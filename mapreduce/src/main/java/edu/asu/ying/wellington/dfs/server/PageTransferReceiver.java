package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import com.healthmarketscience.rmiio.RemoteInputStreamClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import edu.asu.ying.common.concurrency.DelegateQueueExecutor;
import edu.asu.ying.common.concurrency.QueueProcessor;
import edu.asu.ying.wellington.dfs.PageIdentifier;
import edu.asu.ying.wellington.dfs.PageMetadata;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.MemoryPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

/**
 * The {@code PageTransferReceiver} accepts offers for page transfers and returns an output stream
 * to which the remote node can write the page. The output stream is obtained from the
 * {@link Persistence} module designated by {@link DiskPersistence}.
 */
public final class PageTransferReceiver implements QueueProcessor<PageTransfer> {

  private final Persistence memoryPersistence;
  private final Persistence diskPersistence;

  private final DelegateQueueExecutor<PageTransfer> pendingTransfers
      = new DelegateQueueExecutor<PageTransfer>(this);

  @Inject
  private PageTransferReceiver(@MemoryPersistence Persistence memoryPersistence,
                               @DiskPersistence Persistence diskPersistence) {

    this.memoryPersistence = memoryPersistence;
    this.diskPersistence = diskPersistence;
  }

  /**
   * Accepts the transfer into memory, then validates the page.
   * <p/>
   * If the page is valid, commits the page to disk and returns {@link
   * PageTransferResponse.Accepted}.
   */
  public PageTransferResponse offer(PageTransfer transfer) {
    if (pendingTransfers.offer(transfer)) {
      return PageTransferResponse.Accepting;
    } else {
      return PageTransferResponse.Overloaded;
    }
  }

  @Override
  public void process(PageTransfer transfer) {
    PageIdentifier pageId = transfer.getMetadata().getId();
    try (OutputStream ostream
             = memoryPersistence.getOutputStream(pageId)) {
      // Consume the remote stream
      try (InputStream stream = RemoteInputStreamClient.wrap(transfer.getInputStream())) {
        // Read the stream fully
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = stream.read(buffer)) > 0) {
          ostream.write(buffer, 0, read);
        }
      }
    } catch (IOException e) {
      // TODO: Logging
      e.printStackTrace();
      return;
    }

    PageTransferResult result = validateTransfer(transfer.getMetadata());
    if (result == PageTransferResult.Accepted) {
      result = commitToDisk(pageId);
    }
    try {
      transfer.getSendingNode().getDFSService().notifyPageTransferResult(transfer.getId(), result);
    } catch (RemoteException e) {
      // TODO: Logging
      e.printStackTrace();
      // This isn't so bad; we can't reach the node to notify them of our results so we should
      // expect them to re-send soon. If we did get it then we'll return Duplicate.
    }
  }

  /**
   * Reads back the
   */
  private PageTransferResult validateTransfer(PageMetadata metadata) {
  }

  private PageTransferResult commitToDisk(PageIdentifier id) {
    try (InputStream istream = memoryPersistence.getInputStream(id)) {
      try (OutputStream ostream = diskPersistence.getOutputStream(id)) {
        byte[] buffer = new byte[1024];
        int read = 0;
        while ((read = istream.read(buffer)) > 0) {
          ostream.write(buffer, 0, read);
        }
      }
    } catch (IOException e) {
      return PageTransferResult.OtherError;
    }

    return PageTransferResult.Accepted;
  }
}
