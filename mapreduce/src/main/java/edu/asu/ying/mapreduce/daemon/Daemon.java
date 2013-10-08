package edu.asu.ying.mapreduce.daemon;

import java.io.IOException;
import java.net.URI;

import edu.asu.ying.mapreduce.daemon.web.RestInterface;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.node.kad.KadLocalNode;

/**
 *
 */
public final class Daemon {

  private final int port;
  private LocalNode localNode;

  private final DaemonInterface iface;

  public Daemon(final int port) {
    this.port = port;
    try {
      this.localNode = new KadLocalNode(port);
      DaemonSingleton.get(port + 3000).setId(this.localNode.getIdentifier().toString());
    } catch (final InstantiationException e) {
      e.printStackTrace();
    }

    this.iface = new RestInterface(port + 3000);
    try {
      this.iface.startInterface();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public final void join(final Daemon instance) {
    try {
      this.localNode.join(
          URI.create("//127.0.0.1:".concat(String.valueOf(instance.getPort()))));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public final void join(final URI bootstrap) {
    try {
      this.localNode.join(bootstrap);
      // TODO: Logging
      System.out.println(String.format("[%s] <-> [%s]", this.localNode.getIdentifier(),
                                       bootstrap.toString()));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public final LocalNode getLocalNode() {
    return this.localNode;
  }

  public final int getPort() {
    return this.port;
  }
}
