package edu.asu.ying.wellington.rmi;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.dfs.server.RemoteDFSService;
import edu.asu.ying.mapreduce.server.RemoteJobService;
import edu.asu.ying.mapreduce.server.RemoteTaskService;

public final class NodeExporter implements RemoteNode, Exporter<LocalNode, RemoteNode> {

  private final Activator activator;
  private LocalNode node;
  private final Provider<RemoteJobService> jobServiceProvider;
  private final Provider<RemoteTaskService> taskServiceProvider;
  private final Provider<RemoteDFSService> dfsServiceProvider;

  @Inject
  private NodeExporter(Activator activator,
                       Provider<RemoteJobService> jobServiceProvider,
                       Provider<RemoteTaskService> taskServiceProvider,
                       Provider<RemoteDFSService> dfsServiceProvider) {
    // Don't just depend on the Remote* types, because they won't have been exported
    // by the time we get here so we won't have any way of getting the exported proxies.
    // Instead get them lazily from the activator.
    this.activator = activator;
    this.jobServiceProvider = jobServiceProvider;
    this.taskServiceProvider = taskServiceProvider;
    this.dfsServiceProvider = dfsServiceProvider;
  }

  @Override
  public String getName() throws RemoteException {
    return node.getName();
  }

  @Override
  public RemoteJobService getJobService() throws RemoteException {
    return jobServiceProvider.get();
  }

  @Override
  public RemoteTaskService getTaskService() throws RemoteException {
    return taskServiceProvider.get();
  }

  @Override
  public RemoteDFSService getDFSService() throws RemoteException {
    return dfsServiceProvider.get();
  }

  @Override
  public RemoteNode export(LocalNode node) throws ExportException {
    this.node = node;
    return activator.bind(RemoteNode.class, this);
  }
}
