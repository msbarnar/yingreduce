package edu.asu.ying.p2p.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.rmi.RMIActivator;
import edu.asu.ying.common.remoting.rmi.RMIPort;
import edu.asu.ying.p2p.Channel;
import edu.asu.ying.p2p.LocalPeer;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;

/**
 *
 */
public class KadP2PModule extends AbstractModule {

  private static final Logger log = Logger.getLogger(KadP2PModule.class.getName());

  private final Properties properties;

  public KadP2PModule() {
    this(new Properties());
  }

  public KadP2PModule(Properties properties) {
    this.properties = getDefaultProperties();
    this.properties.putAll(properties);
  }

  public KadP2PModule setProperty(String key, String value) {
    this.properties.setProperty(key, value);
    return this;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);

    KeybasedRouting kbr;
    try {
      kbr = createKeybasedRouting(Integer.parseInt(properties.getProperty("p2p.port")));
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    }

    bind(KeybasedRouting.class).toInstance(kbr);

    // P2P Network
    bind(Activator.class).to(RMIActivator.class).in(Scopes.SINGLETON);
    bind(Channel.class).to(KadChannel.class).in(Scopes.SINGLETON);
    bind(LocalPeer.class).to(KadLocalPeer.class).in(Scopes.SINGLETON);
  }

  private static KeybasedRouting createKeybasedRouting(final int port)
      throws InstantiationException {

    final Injector injector = Guice.createInjector(
        new KadNetModule()
            .setProperty("openkad.keyfactory.keysize", String.valueOf(20))
            .setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(20))
            .setProperty("openkad.seed", String.valueOf(port))
            .setProperty("openkad.net.udp.port", String.valueOf(port))
            .setProperty("openkad.file.nodes.path",
                         System.getProperty("user.home").concat("/.kadhosts"))
    );

    final KeybasedRouting kadNode = injector.getInstance(KeybasedRouting.class);
    try {
      kadNode.create();
    } catch (final IOException e) {
      log.log(Level.SEVERE, "Failed to create local Kademlia node", e);
      throw new InstantiationException("Failed to create local Kademlia node");
    }

    return kadNode;
  }

  private int rmiPort = 0;
  private final Object portLock = new Object();

  /**
   * Provides a constant port, random by default, set by the property {@code rmi.port}.
   */
  @Provides
  @RMIPort
  private int provideRMIPort() {
    if (rmiPort <= 0) {
      synchronized (portLock) {
        if (rmiPort <= 0) {
          try {
            rmiPort = Integer.parseInt(properties.getProperty("rmi.port"));
            if (rmiPort <= 0) {
              throw new NumberFormatException();
            }
          } catch (NumberFormatException | NullPointerException e) {
            // Get a random port
            ServerSocket sock;
            try {
              sock = new ServerSocket(0);
              rmiPort = sock.getLocalPort();
              sock.close();
            } catch (final IOException ex) {
              e.printStackTrace();
              throw new RuntimeException(e);
            }
          }
        }
      }
    }
    return rmiPort;
  }

  private Properties getDefaultProperties() {
    Properties props = new Properties();

    props.setProperty("p2p.port", "5000");

    return props;
  }
}
