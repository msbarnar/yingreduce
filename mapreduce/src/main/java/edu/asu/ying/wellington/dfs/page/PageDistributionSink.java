package edu.asu.ying.wellington.dfs.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadPeerIdentifier;
import edu.asu.ying.p2p.rmi.RemoteImportException;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink implements Sink<Page> {

  private static final int DEFAULT_DUPLICATION_FACTOR = 3;

  private final LocalPeer localPeer;
  private final int pageDuplicationFactor = DEFAULT_DUPLICATION_FACTOR;

  // Used to offer a page to several peers at once
  private final ExecutorService peerWorkers = Executors.newCachedThreadPool();
  // Used to offer several pages at once
  private final ExecutorService pageWorkers = Executors.newFixedThreadPool(3);


  public PageDistributionSink(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  /**
   * Finds k peers near the page's key and concurrently offers them the page.
   */
  @Override
  public boolean offer(final Page page) throws IOException {
    final List<RemotePeer> peers = this.findPeers(page);
    final List<Future<Boolean>> results = new ArrayList<>(peers.size());

    // Concurrently offer the page to all of the peers
    for (final RemotePeer peer : peers) {
      results.add(this.peerWorkers.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          // FIXME: BROKEN FOR TESTING
          //return peer.getPageSink().offer(page);
          return false;
        }
      }));
    }

    // Wait for each peer to return
    boolean success = true;
    for (final Future<Boolean> result : results) {
      try {
        success &= result.get();
      } catch (final InterruptedException | ExecutionException e) {
        // TODO: Logging
        e.printStackTrace();
        return false;
      }
    }
    return success;
  }

  /**
   * For each {@link Page} in {@code pages}, finds k peers near the page's key and concurrently
   * offers them the page.
   *
   * @return the number of pages successfully sent.
   */
  @Override
  public int offer(final Iterable<Page> pages) throws IOException {
    final List<Future<Boolean>> results = new ArrayList<>();
    for (final Page page : pages) {
      results.add(this.pageWorkers.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          return PageDistributionSink.this.offer(page);
        }
      }));
    }

    int count = 0;
    for (final Future<Boolean> result : results) {
      try {
        count += result.get() ? 1 : 0;
      } catch (final InterruptedException | ExecutionException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
    return count;
  }

  private List<RemotePeer> findPeers(final Page page)
      throws PeerNotFoundException, RemoteImportException {
    // Tags: PAGE KEY IDENTIFIER ROUTING ID
    final PeerIdentifier id =
        new KadPeerIdentifier(page.getTableId().toString().concat(String.valueOf(page.getIndex())));
    return this.localPeer.findPeers(id, this.pageDuplicationFactor);
  }
}
