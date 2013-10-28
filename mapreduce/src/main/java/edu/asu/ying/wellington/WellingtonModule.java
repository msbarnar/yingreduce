package edu.asu.ying.wellington;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Properties;

import edu.asu.ying.common.concurrency.QueueExecutor;
import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.ClassNotExportedException;
import edu.asu.ying.common.remoting.Local;
import edu.asu.ying.common.remoting.Remote;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.persistence.CachePersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistence;
import edu.asu.ying.wellington.dfs.persistence.DiskPersistenceConnector;
import edu.asu.ying.wellington.dfs.persistence.Persistence;
import edu.asu.ying.wellington.dfs.persistence.PersistenceConnector;
import edu.asu.ying.wellington.dfs.persistence.PersistenceEngine;
import edu.asu.ying.wellington.dfs.persistence.SimpleCachePersistenceConnector;
import edu.asu.ying.wellington.dfs.server.DFSServer;
import edu.asu.ying.wellington.dfs.server.PageDistributionSink;
import edu.asu.ying.wellington.dfs.server.PageDistributor;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.job.Job;
import edu.asu.ying.wellington.mapreduce.job.JobDelegator;
import edu.asu.ying.wellington.mapreduce.job.JobScheduler;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.job.Jobs;
import edu.asu.ying.wellington.mapreduce.server.RemoteJobService;
import edu.asu.ying.wellington.mapreduce.server.RemoteTaskService;
import edu.asu.ying.wellington.mapreduce.task.Forwarding;
import edu.asu.ying.wellington.mapreduce.task.Task;
import edu.asu.ying.wellington.mapreduce.task.TaskScheduler;
import edu.asu.ying.wellington.mapreduce.task.TaskService;
import edu.asu.ying.wellington.mapreduce.task.execution.ForwardingQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.execution.LocalQueueExecutor;
import edu.asu.ying.wellington.mapreduce.task.execution.RemoteQueueExecutor;

/**
 * {@code WellingtonModule} binds Wellington service classes. </p> The module depends on {@link
 * edu.asu.ying.p2p.LocalPeer} and {@link Activator} being bound, so it must be used to create a
 * child injector of a module that binds those classes:
 * <pre>
 *   {@code
 *    Injector injector = Guice.createInjector(new KadP2PModule())
 *      .createChildInjector(new WellingtonModule());
 * }
 * </pre>
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
    configureJobService();
    configureTaskService();
    configureDFSService();
  }

  private void configureServiceNetwork() {
    bind(NodeImpl.class).in(Scopes.SINGLETON);
    bind(LocalNode.class).to(NodeImpl.class);
    bind(NodeLocator.class).to(NodeImpl.class);
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
    bind(PageDistributor.class)
        .to(PageDistributionSink.class)
        .in(Scopes.SINGLETON);

    bind(Persistence.class)
        .to(PersistenceEngine.class)
        .in(Scopes.SINGLETON);
    bind(PersistenceConnector.class)
        .annotatedWith(DiskPersistence.class)
        .to(DiskPersistenceConnector.class)
        .in(Scopes.SINGLETON);

    bind(PersistenceConnector.class)
        .annotatedWith(CachePersistence.class)
        .to(SimpleCachePersistenceConnector.class)
        .in(Scopes.SINGLETON);
  }

  @Provides
  @Local
  private RemoteNode provideLoopbackProxy(Activator activator) throws ClassNotExportedException {
    return activator.getReference(RemoteNode.class);
  }

  @Provides
  private RemoteJobService provideJobServiceProxy(Activator activator)
      throws ClassNotExportedException {
    return activator.getReference(RemoteJobService.class);
  }

  @Provides
  private RemoteTaskService provideTaskServiceProxy(Activator activator)
      throws ClassNotExportedException {
    return activator.getReference(RemoteTaskService.class);
  }

  @Provides
  private RemoteDFSService provideDFSServiceProxy(Activator activator)
      throws ClassNotExportedException {
    return activator.getReference(RemoteDFSService.class);
  }

  @Provides
  @Local
  private String provideLocalNodeID(LocalNode localNode) {
    return localNode.getName();
  }

  private Properties getDefaultProperties() {
    Properties defaults = new Properties();

    defaults.put("dfs.store.path", System.getProperty("user.home").concat("/dfs"));
    defaults.put("dfs.page.replication", "3");

    return defaults;
  }
}
