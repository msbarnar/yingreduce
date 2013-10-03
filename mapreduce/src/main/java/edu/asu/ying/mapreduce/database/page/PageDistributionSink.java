package edu.asu.ying.mapreduce.database.page;

import java.io.IOException;
import java.net.UnknownHostException;

import edu.asu.ying.mapreduce.common.Sink;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;
import edu.asu.ying.p2p.node.kad.KadNodeIdentifier;

/**
 * {@code PageDistributionSink} distributes accepted pages to appropriate peers on the network.
 */
public final class PageDistributionSink implements Sink<Page> {

  private final LocalNode localNode;

  public PageDistributionSink(final LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  public final void accept(final Page page) throws IOException {
    final RemoteNode peer = this.findNode(page);
    final Sink<Page> remotePageSink = peer.getPageSink();
    remotePageSink.accept(page);
  }

  private RemoteNode findNode(final Page page) throws UnknownHostException {
    // Tags: PAGE KEY IDENTIFIER ROUTING ID
    final NodeIdentifier id =
        new KadNodeIdentifier(page.getTableId().toString().concat(String.valueOf(page.getIndex())));
    return this.localNode.findNode(id);
  }
}
