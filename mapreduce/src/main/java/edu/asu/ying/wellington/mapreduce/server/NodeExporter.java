package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.Exporter;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

public final class NodeExporter implements RemoteNode, Exporter<LocalNode, RemoteNode> {

  private final Activator activator;
  private LocalNode node;
  private final JobService jobService;
  private final TaskService taskService;
  private final DFSService dfsService;

  @Inject
  private NodeExporter(Activator activator,
                       JobService jobService,
                       TaskService taskService,
                       DFSService dfsService) {

    this.activator = activator;

    // Don't just depend on the Remote* types, because they won't have been exported
    // by the time we get here so we won't have any way of getting the exported proxies.
    // Instead get them lazily from the services themselves, whose creation guarantees that the
    // proxies are exported.
    this.jobService = jobService;
    this.taskService = taskService;
    this.dfsService = dfsService;
  }

  @Override
  public NodeIdentifier getIdentifier() throws RemoteException {
    return node.getID();
  }

  @Override
  public RemoteJobService getJobService() throws RemoteException {
    return jobService.asRemote();
  }

  @Override
  public RemoteTaskService getTaskService() throws RemoteException {
    return taskService.asRemote();
  }

  @Override
  public RemoteDFSService getDFSService() throws RemoteException {
    return dfsService.asRemote();
  }

  @Override
  public RemoteNode export(LocalNode node) throws ExportException {
    this.node = node;
    return activator.bind(RemoteNode.class, this);
  }
}
