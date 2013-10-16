package edu.asu.ying.wellington.daemon;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.IOException;
import java.net.URI;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.wellington.daemon.web.RestInterface;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;

/**
 *
 */
public final class Daemon {

  private final LocalPeer peer;
  private final LocalNode node;
  private final int port;

  private final DaemonInterface iface;

  @Inject
  private Daemon(LocalPeer peer,
                 LocalNode node,
                 @Named("p2p.port") int port) {

    this.peer = peer;
    this.node = node;
    this.port = port;

    this.iface = new RestInterface(port + 3000);
    try {
      iface.startInterface();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }

  public void join(Daemon instance) {
    try {
      peer.join(
          URI.create("//127.0.0.1:".concat(String.valueOf(instance.getPort()))));
      System.out.println(String.format("[%d] -> [%d]", port, instance.getPort()));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public void join(URI bootstrap) {
    try {
      peer.join(bootstrap);
      // TODO: Logging
      System.out.println(String.format("%s <-> %s", peer.getIdentifier(),
                                       bootstrap.toString()));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public LocalPeer getPeer() {
    return peer;
  }

  public int getPort() {
    return port;
  }
}
