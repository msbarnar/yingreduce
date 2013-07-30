package edu.asu.ying.mapreduce.node.kad;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.kad.KadChannel;
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
    );

    final KeybasedRouting kadNode = injector.getInstance(KeybasedRouting.class);
    try {
      kadNode.create();
    } catch (final IOException e) {
      throw new InstantiationException("Failed to create local Kademlia node");
    }

    return kadNode;
  }

  public static Channel createChannel(final KeybasedRouting kbrNode) {
    return new KadChannel(kbrNode);
  }
}
