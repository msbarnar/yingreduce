package edu.asu.ying.wellington.daemon;

import java.io.IOException;
import java.net.URI;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.wellington.daemon.web.RestInterface;

/**
 *
 */
public final class Daemon {

  private final int port;
  private LocalPeer localPeer;

  private final DaemonInterface iface;

  public Daemon(final int port) {
    this.port = port;
    try {
      // FIXME: BROKEN FOR TESTING
      this.localPeer = null;//new KadLocalPeer(port);
      throw new InstantiationException();
      //DaemonSingleton.get(port + 3000).setId(this.localPeer.getIdentifier().toString());
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
      this.localPeer.join(
          URI.create("//127.0.0.1:".concat(String.valueOf(instance.getPort()))));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public final void join(final URI bootstrap) {
    try {
      this.localPeer.join(bootstrap);
      // TODO: Logging
      System.out.println(String.format("[%s] <-> [%s]", this.localPeer.getIdentifier(),
                                       bootstrap.toString()));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public final LocalPeer getLocalPeer() {
    return this.localPeer;
  }

  public final int getPort() {
    return this.port;
  }
}
