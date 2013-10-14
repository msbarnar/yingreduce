package edu.asu.ying.wellington;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import java.util.Properties;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.kad.KadLocalPeer;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.server.DFSServer;
import edu.asu.ying.wellington.mapreduce.job.JobScheduler;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;
import edu.asu.ying.wellington.mapreduce.server.NodeServer;
import edu.asu.ying.wellington.mapreduce.task.TaskScheduler;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

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
    Names.bindProperties(this.binder(), this.properties);
    // P2P Network
    this.bind(LocalPeer.class).to(KadLocalPeer.class);
    // Service network
    this.bind(LocalNode.class).to(NodeServer.class);
    // Services
    this.bind(JobService.class).to(JobScheduler.class);
    this.bind(TaskService.class).to(TaskScheduler.class);
    this.bind(DFSService.class).to(DFSServer.class);
  }

  private Properties getDefaultProperties() {
    Properties props = new Properties();

    props.setProperty("p2p.port", "5000");

    return props;
  }
}
