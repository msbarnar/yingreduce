package edu.asu.ying.wellington;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Properties;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.event.Sink;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.PageDistributor;
import edu.asu.ying.wellington.dfs.client.PageDistributionSink;
import edu.asu.ying.wellington.dfs.server.DFSServer;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobDelegator;
import edu.asu.ying.wellington.mapreduce.job.JobScheduler;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.job.Jobs;
import edu.asu.ying.wellington.mapreduce.server.LocalNode;
import edu.asu.ying.wellington.mapreduce.server.NodeIdentifier;
import edu.asu.ying.wellington.mapreduce.server.NodeImpl;
import edu.asu.ying.wellington.mapreduce.server.NodeLocator;
import edu.asu.ying.wellington.mapreduce.task.Forwarding;
import edu.asu.ying.wellington.mapreduce.task.Local;
import edu.asu.ying.wellington.mapreduce.task.Remote;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskScheduler;
import edu.asu.ying.wellington.mapreduce.task.TaskService;
import edu.asu.ying.wellington.mapreduce.task.exc.ForwardingQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.exc.LocalQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.exc.RemoteQueueExecutor;

/**
 * {@code WellingtonModule} provides the bindings for dependency injection.
 */
public final class WellingtonModule extends AbstractModule {

  private final Properties properties;

  public WellingtonModule() {
    this(new Properties());
  }

  public WellingtonModule(Properties properties) {
    this.properties = getDefaultProperties();
    this.properties.putAll(properties);
  }

  public WellingtonModule setProperty(String key, String value) {
    this.properties.setProperty(key, value);
    return this;
  }

  @Override
  protected void configure() {
    Names.bindProperties(binder(), properties);

    configureServiceNetwork();
    configureServices();
  }

  private void configureServiceNetwork() {
    bind(LocalNode.class).to(NodeImpl.class).in(Scopes.SINGLETON);
    bind(NodeLocator.class).to(NodeImpl.class).in(Scopes.SINGLETON);
  }

  private void configureServices() {
    configureJobService();
    configureTaskService();
    configureDFSService();
  }

  private void configureJobService() {
    bind(JobService.class).to(JobScheduler.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<QueueExecutor<Job>>() {
    })
        .annotatedWith(Jobs.class)
        .to(JobDelegator.class)
        .in(Scopes.SINGLETON);
  }

  private void configureTaskService() {
    bind(TaskService.class).to(TaskScheduler.class).in(Scopes.SINGLETON);
    bind(new TypeLiteral<QueueExecutor<Task>>() {
    })
        .annotatedWith(Forwarding.class)
        .to(ForwardingQueueExecutor.class)
        .in(Scopes.SINGLETON);
    bind(new TypeLiteral<QueueExecutor<Task>>() {
    })
        .annotatedWith(Remote.class)
        .to(RemoteQueueExecutor.class)
        .in(Scopes.SINGLETON);
    bind(new TypeLiteral<QueueExecutor<Task>>() {
    })
        .annotatedWith(Local.class)
        .to(LocalQueueExecutor.class)
        .in(Scopes.SINGLETON);
  }

  private void configureDFSService() {
    bind(DFSService.class).to(DFSServer.class).in(Scopes.SINGLETON);
    bind(Sink.class)
        .annotatedWith(PageDistributor.class)
        .to(PageDistributionSink.class)
        .in(Scopes.SINGLETON);
  }

  @Provides
  @Local
  private NodeIdentifier provideLocalNodeID(LocalNode localNode) {
    return localNode.getID();
  }

  private Properties getDefaultProperties() {
    Properties props = new Properties();
    return props;
  }
}
