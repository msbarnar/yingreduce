package edu.asu.ying.database.daemon;

import java.rmi.server.ExportException;

import edu.asu.ying.common.event.Sink;
import edu.asu.ying.database.page.IncomingPageHandler;
import edu.asu.ying.database.page.Page;
import edu.asu.ying.database.page.PageDistributionSink;
import edu.asu.ying.database.page.RemotePageSink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.rmi.RemotePageSinkProxy;

/**
 * {@code DatabaseDaemon} is the interface between the database and the network.
 */
public final class DatabaseDaemon {

  private final LocalPeer localPeer;

  // Sends pages to the network
  private final Sink<Page> pageOutSink;
  // Accepts pages from the network
  private final IncomingPageHandler pageInSink;


  public DatabaseDaemon(final LocalPeer localPeer) throws InstantiationException {
    this.localPeer = localPeer;

    // Open the outgoing page pipe
    this.pageOutSink = new PageDistributionSink(this.localPeer);

    // Open the incoming page pipe
    this.pageInSink = new IncomingPageHandler();
    try {
      this.localPeer.getActivator().bind(RemotePageSink.class).via(RemotePageSinkProxy.class)
          .toInstance(this.pageInSink);
    } catch (final ExportException e) {
      throw new InstantiationException("Failed to export server page sink");
    }
  }
}
