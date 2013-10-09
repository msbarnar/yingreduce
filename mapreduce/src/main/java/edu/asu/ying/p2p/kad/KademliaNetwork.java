package edu.asu.ying.p2p.kad;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

import edu.asu.ying.p2p.io.Channel;
import edu.asu.ying.p2p.io.kad.KadChannel;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;

/**
 *
 */
public final class KademliaNetwork {

  public static KeybasedRouting createNode(final int port) throws InstantiationException {
    final Injector injector = Guice.createInjector(
        new KadNetModule()
            .setProperty("openkad.keyfactory.keysize", String.valueOf(16))
            .setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(16))
            .setProperty("openkad.seed", String.valueOf(port))
            .setProperty("openkad.net.udp.port", String.valueOf(port))
            .setProperty("openkad.file.nodes.path",
                         System.getProperty("user.home").concat("/.kadhosts"))
    );

    final KeybasedRouting kadNode = injector.getInstance(KeybasedRouting.class);
    try {
      kadNode.create();
    } catch (final IOException e) {
      e.printStackTrace();
      throw new InstantiationException("Failed to create local Kademlia node");
    }

    return kadNode;
  }

  public static Channel createChannel(final KeybasedRouting kbrNode) {
    return new KadChannel(kbrNode);
  }
}
