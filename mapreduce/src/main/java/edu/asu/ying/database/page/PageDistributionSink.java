package edu.asu.ying.database.page;

import java.io.IOException;

import edu.asu.ying.common.sink.Sink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.kad.KadPeerIdentifier;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink implements Sink<Page> {

  private final LocalPeer localPeer;

  public PageDistributionSink(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public boolean offer(final Page page) throws IOException {
    return this.findPeer(page).getPageSink().offer(page);
  }

  @Override
  public int offer(Iterable<Page> pages) throws IOException {
    int count = 0;
    for (final Page page : pages) {
      count += this.offer(page) ? 1 : 0;
    }
    return count;
  }

  private RemotePeer findPeer(final Page page) throws PeerNotFoundException {
    // Tags: PAGE KEY IDENTIFIER ROUTING ID
    final PeerIdentifier id =
        new KadPeerIdentifier(page.getTableId().toString().concat(String.valueOf(page.getIndex())));
    return this.localPeer.findPeer(id);
  }
}
