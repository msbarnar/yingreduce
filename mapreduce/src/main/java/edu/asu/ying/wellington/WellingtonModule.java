package edu.asu.ying.wellington;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import java.io.IOException;
import java.util.Properties;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.kad.KadLocalPeer;
import edu.asu.ying.p2p.net.Channel;
import edu.asu.ying.p2p.net.kad.KadChannel;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.dfs.client.PageDistributionSink;
import edu.asu.ying.wellington.dfs.server.DFSServer;
import edu.asu.ying.wellington.mapreduce.job.JobDelegator;
import edu.asu.ying.wellington.mapreduce.job.JobScheduler;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.job.Jobs;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;
import edu.asu.ying.wellington.mapreduce.server.LocalNodeProxy;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.server.NodeServer;
import edu.asu.ying.wellington.mapreduce.server.RemoteNode;
import edu.asu.ying.wellington.mapreduce.task.Forwarding;
import edu.asu.ying.wellington.mapreduce.task.ForwardingQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Local;
import edu.asu.ying.wellington.mapreduce.task.LocalQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.Remote;
import edu.asu.ying.wellington.mapreduce.task.RemoteQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.TaskScheduler;
import edu.asu.ying.wellington.mapreduce.task.TaskService;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.openkad.KadNetModule;

/**
 * {@code WellingtonModule} provides the bindings for dependency injection.
 */
public final class WellingtonModule extends AbstractModule {

  private final Properties properties;

  public WellingtonModule() {
    this(new Properties());
  }

  public WellingtonModule(Properties properties) {
    this.properties = this.getDefaultProperties();
    this.properties.putAll(properties);
  }

  public WellingtonModule setProperty(String key, String value) {
    this.properties.setProperty(key, value);
    return this;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);

    KeybasedRouting keybasedRouting = null;
    try {
      keybasedRouting = createKeybasedRouting(Integer.parseInt(properties.getProperty("p2p.port")));
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    }

    // P2P Network
    bind(KeybasedRouting.class).toInstance(keybasedRouting);
    bind(Channel.class).to(KadChannel.class).in(Scopes.SINGLETON);
    bind(LocalPeer.class).to(KadLocalPeer.class).in(Scopes.SINGLETON);

    // Service network
    bind(LocalNode.class).to(NodeServer.class).in(Scopes.SINGLETON);
    bind(NodeLocator.class).to(NodeServer.class).in(Scopes.SINGLETON);

    // Services
    // Jobs
    bind(JobService.class).to(JobScheduler.class).in(Scopes.SINGLETON);
    bind(QueueExecutor.class)
        .annotatedWith(Jobs.class)
        .to(JobDelegator.class)
        .in(Scopes.SINGLETON);

    // Task Execution
    bind(TaskService.class).to(TaskScheduler.class).in(Scopes.SINGLETON);
    bind(QueueExecutor.class)
        .annotatedWith(Forwarding.class)
        .to(ForwardingQueueExecutor.class)
        .in(Scopes.SINGLETON);
    bind(QueueExecutor.class)
        .annotatedWith(Remote.class)
        .to(RemoteQueueExecutor.class)
        .in(Scopes.SINGLETON);
    bind(QueueExecutor.class)
        .annotatedWith(Local.class)
        .to(LocalQueueExecutor.class)
        .in(Scopes.SINGLETON);

    // DFS
    bind(Sink.class)
        .annotatedWith(PageDistributor.class)
        .to(PageDistributionSink.class)
        .in(Scopes.SINGLETON);
    bind(DFSService.class).to(DFSServer.class).in(Scopes.SINGLETON);
  }

  @Provides
  @LocalNodeProxy
  private RemoteNode provideRemoteNode(LocalNode localNode) {
    return localNode.getAsRemote();
  }

  private Properties getDefaultProperties() {
    Properties props = new Properties();

    props.setProperty("p2p.port", "5000");

    return props;
  }

  private static KeybasedRouting createKeybasedRouting(final int port)
      throws InstantiationException {

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
}
