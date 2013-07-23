package edu.asu.ying.mapreduce.net.kad;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import java.io.IOException;
import java.util.Random;

import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.io.SendMessageStream;
import edu.asu.ying.mapreduce.io.kad.KadSendMessageStream;
import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import edu.asu.ying.mapreduce.net.messaging.kad.KadMessageHandler;
import edu.asu.ying.mapreduce.rmi.Activator;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;


/**
 * The {@code KademliaModule} wires all of the high-level operations (e.g. {@link
 * edu.asu.ying.mapreduce.rmi.ActivatorFinder}) to the underlying Kademlia
 * network classes.
 */
public final class KademliaModule
    extends AbstractModule {

  // Singleton local node
  private static final Object localNodeLock = new Object();
  private static LocalNode localNode;

  /**
   * Singleton {@link KeybasedRouting} provider for all Kademlia traffic
   */
  private enum KadNodeProvider {
    INSTANCE;
    private final KeybasedRouting kadNode;

    private KadNodeProvider() {
      final int port = 5000 + (new Random()).nextInt(1000);
      final Injector injector = Guice.createInjector(new KadNetModule()
                                 .setProperty("openkad.keyfactory.keysize", String.valueOf(20))
                                 .setProperty("openkad.bucket.kbuckets.maxsize", String.valueOf(20))
                                 .setProperty("openkad.seed", String.valueOf(port))
                                 .setProperty("openkad.net.udp.port", String.valueOf(port)));
      this.kadNode = injector.getInstance(KeybasedRouting.class);
      try {
        this.kadNode.create();
      } catch (final IOException e) {
        throw new ExceptionInInitializerError(e);
      }
    }
  }

  @Override
  protected void configure() {
    // OpenKAD node interface
    bind(KeybasedRouting.class).toInstance(KadNodeProvider.INSTANCE.kadNode);

    // Kad Message Sending
    bind(MessageOutputStream.class).annotatedWith(SendMessageStream.class)
        .to(KadSendMessageStream.class);

    // RMI
    bind(Activator.class).toInstance(this.provideLocalNode().getActivator());

    // Task Scheduling
    bind(Scheduler.class).to(SchedulerImpl.class);
  }

  @Provides
  private LocalNode provideLocalNode() {
    // Double locked singleton
    if (KademliaModule.localNode == null) {
      synchronized (KademliaModule.localNodeLock) {
        if (KademliaModule.localNode == null) {
          final Injector injector = Guice.createInjector(this);
          KademliaModule.localNode = injector.getInstance(KadLocalNode.class);
        }
      }
    }
    return KademliaModule.localNode;
  }
}
