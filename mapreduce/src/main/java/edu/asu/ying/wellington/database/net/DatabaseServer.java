package edu.asu.ying.wellington.database.net;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.wellington.database.page.IncomingPageHandler;
import edu.asu.ying.wellington.database.page.Page;
import edu.asu.ying.wellington.database.page.PageDistributionSink;

/**
 * {@code DatabaseServer} is the interface between the database and the network.
 */
public final class DatabaseServer {

  private final LocalPeer localPeer;

  // Sends pages to the network
  private final Sink<Page> pageOutSink;
  // Accepts pages from the network
  private final IncomingPageHandler pageInSink;


  public DatabaseServer(final LocalPeer localPeer) throws InstantiationException {
    this.localPeer = localPeer;

    // Open the outgoing page pipe
    this.pageOutSink = new PageDistributionSink(this.localPeer);

    // Open the incoming page pipe
    this.pageInSink = new IncomingPageHandler();
    /*try {
      this.localPeer.getActivator().bind(RemotePageSink.class).to(this.pageInSink)
          .wrappedBy(RemotePageSinkProxy.class);
    } catch (final ExportException e) {
      throw new InstantiationException("Failed to export server page sink");
    }*/
  }
}
