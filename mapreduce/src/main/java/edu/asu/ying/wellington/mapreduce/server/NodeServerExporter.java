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

public final class NodeServerExporter implements RemoteNode, Exporter<LocalNode, RemoteNode> {

  private final Activator activator;
  private LocalNode localNode;
  private final JobService jobService;
  private final TaskService taskService;
  private final DFSService dfsService;

  @Inject
  private NodeServerExporter(Activator activator,
                             JobService jobService,
                             TaskService taskService,
                             DFSService dfsService) {

    this.activator = activator;

    this.jobService = jobService;
    this.taskService = taskService;
    this.dfsService = dfsService;
  }

  @Override
  public NodeIdentifier getIdentifier() throws RemoteException {
    return localNode.getID();
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
  public RemoteNode export(LocalNode node) {
    this.localNode = node;
    try {
      return activator.bind(RemoteNode.class, this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }
  }
}
