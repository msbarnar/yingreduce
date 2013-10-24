package edu.asu.ying.wellington.dfs.server;

import com.google.inject.Inject;

import java.io.IOException;

import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.Persistence;

/**
 * The {@code PageTransferReceiver} accepts offers for page transfers and returns an output stream
 * to which the remote node can write the page. The output stream is obtained from the
 * {@link Persistence} module designated by {@link DiskPersistence}.
 */
public final class PageTransferReceiver {

  private final Persistence persistence;

  @Inject
  private PageTransferReceiver(@DiskPersistence Persistence persistence) {
    this.persistence = persistence;
  }

  public PageTransferResult offer(PageTransfer transfer) throws IOException {
  }
}
