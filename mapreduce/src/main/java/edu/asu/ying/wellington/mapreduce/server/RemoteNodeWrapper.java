package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

public final class RemoteNodeWrapper implements RemoteNode, Wrapper<RemoteNode, LocalNode> {

  // Keep a strong reference to the proxy
  private final RemoteNode proxyInstance;

  private LocalNode localNode;
  private final RemoteJobService jobServiceProxy;
  private final RemoteTaskService taskServiceProxy;
  private final RemoteDFSService dfsServiceProxy;

  @Inject
  private RemoteNodeWrapper(Activator activator,
                            RemoteJobService jobService,
                            RemoteTaskService taskService,
                            RemoteDFSService dfsService) {

    try {
      this.proxyInstance = activator.bind(RemoteNode.class, this);
    } catch (ExportException e) {
      // TODO: Logging
      throw new RuntimeException(e);
    }

    this.jobServiceProxy = jobService;
    this.taskServiceProxy = taskService;
    this.dfsServiceProxy = dfsService;
  }

  @Override
  public void wrap(LocalNode target) throws RemoteException {
    this.localNode = target;
  }

  @Override
  public NodeIdentifier getIdentifier() throws RemoteException {
    return localNode.getID();
  }

  @Override
  public RemoteJobService getJobService() throws RemoteException {
    return jobServiceProxy;
  }

  @Override
  public RemoteTaskService getTaskService() throws RemoteException {
    return taskServiceProxy;
  }

  @Override
  public RemoteDFSService getDFSService() throws RemoteException {
    return dfsServiceProxy;
  }
}
