package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.rmi.RemoteException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.Wrapper;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

public final class RemoteNodeWrapper implements RemoteNode, Wrapper<RemoteNode> {

  // Keep a strong reference to the proxy
  private final RemoteNode proxyInstance;

  private final LocalNode localNode;
  private final RemoteJobService jobServiceProxy;
  private final RemoteTaskService taskServiceProxy;
  private final RemoteDFSService dfsServiceProxy;

  @Inject
  private RemoteNodeWrapper(LocalNode localNode,
                            Activator activator,
                            RemoteJobService jobService,
                            RemoteTaskService taskService,
                            RemoteDFSService dfsService) {

    this.proxyInstance = activator.bind(RemoteNode.class).toInstance(this);

    this.localNode = localNode;
    this.jobServiceProxy = jobService;
    this.taskServiceProxy = taskService;
    this.dfsServiceProxy = dfsService;
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

  @Override
  public RemoteNode getProxy() {
    return proxyInstance;
  }
}
