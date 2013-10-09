package edu.asu.ying.mapreduce.database.page;

import java.io.IOException;
import java.net.UnknownHostException;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.peer.kad.KadPeerIdentifier;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink implements Sink<Page> {

  private final LocalPeer localPeer;

  public PageDistributionSink(final LocalPeer localPeer) {
    this.localPeer = localPeer;
  }

  @Override
  public final void accept(final Page page) throws IOException {
    final RemotePeer peer = this.findNode(page);
    final Sink<Page> remotePageSink = peer.getPageSink();
    remotePageSink.accept(page);
  }

  private RemotePeer findNode(final Page page) throws UnknownHostException {
    // Tags: PAGE KEY IDENTIFIER ROUTING ID
    final PeerIdentifier id =
        new KadPeerIdentifier(page.getTableId().toString().concat(String.valueOf(page.getIndex())));
    return this.localPeer.findNode(id);
  }
}
