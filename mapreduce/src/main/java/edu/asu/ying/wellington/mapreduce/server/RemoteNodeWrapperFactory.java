package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.dfs.server.DFSServiceWrapperFactory;
import edu.asu.ying.wellington.dfs.server.RemoteDFSService;

/**
 *
 */
public final class RemoteNodeWrapperFactory implements WrapperFactory<NodeServer, RemoteNode> {

  @Override
  public RemoteNode create(NodeServer target, Activator activator) {
    return new RemoteNodeWrapper(target, activator);
  }

  private final class RemoteNodeWrapper implements RemoteNode {

    private final LocalNode localNode;
    private final RemoteJobService jobServiceProxy;
    private final RemoteTaskService taskServiceProxy;
    private final RemoteDFSService dfsServiceProxy;

    private RemoteNodeWrapper(LocalNode localNode, Activator activator) {
      this.localNode = localNode;

      try {
        this.jobServiceProxy = activator.bind(RemoteJobService.class)
            .to(localNode.getJobService())
            .wrappedBy(new JobServiceWrapperFactory());

        this.taskServiceProxy = activator.bind(RemoteTaskService.class)
            .to(localNode.getTaskService())
            .wrappedBy(new TaskServiceWrapperFactory());

        this.dfsServiceProxy = activator.bind(RemoteDFSService.class)
            .to(localNode.getDFSService())
            .wrappedBy(new DFSServiceWrapperFactory());

      } catch (ExportException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public NodeIdentifier getIdentifier() throws RemoteException {
      return localNode.getId();
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
}
