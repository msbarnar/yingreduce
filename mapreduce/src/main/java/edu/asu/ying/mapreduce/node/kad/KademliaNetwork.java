package edu.asu.ying.mapreduce.node.kad;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

import edu.asu.ying.mapreduce.node.io.Channel;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;

/**
 *
 */
public final class KademliaNetwork {

  public static KeybasedRouting createNode(final int port) {
    final Injector injector = Guice.createInjector(new KadNetModule()
                                                       .setProperty("openkad.keyfactory.keysize",
                                                                    String.valueOf(20))
                                                       .setProperty(
                                                           "openkad.bucket.kbuckets.maxsize",
                                                           String.valueOf(20))
                                                       .setProperty("openkad.seed",
                                                                    String.valueOf(port))
                                                       .setProperty("openkad.net.udp.port",
                                                                    String.valueOf(port)));

    final KeybasedRouting kadNode = injector.getInstance(KeybasedRouting.class);
    try {
      kadNode.create();
    } catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    return kadNode;
  }

  public static Channel createChannel(final KeybasedRouting kbrNode) {

  }
}
