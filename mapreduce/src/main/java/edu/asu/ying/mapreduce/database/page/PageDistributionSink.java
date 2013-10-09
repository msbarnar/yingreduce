package edu.asu.ying.mapreduce.database.page;

import java.io.IOException;

import edu.asu.ying.mapreduce.common.Sink;
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
  public final void offer(final Page page) throws IOException {
    final RemotePeer peer = this.findPeer(page);
    final Sink<Page> remotePageSink = peer.getPageSink();
    remotePageSink.offer(page);
  }

  private RemotePeer findPeer(final Page page) throws PeerNotFoundException {
    // Tags: PAGE KEY IDENTIFIER ROUTING ID
    final PeerIdentifier id =
        new KadPeerIdentifier(page.getTableId().toString().concat(String.valueOf(page.getIndex())));
    return this.localPeer.findPeer(id);
  }
}
