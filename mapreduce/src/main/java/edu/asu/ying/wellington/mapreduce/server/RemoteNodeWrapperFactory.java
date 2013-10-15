package edu.asu.ying.wellington.mapreduce.server;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;

import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;

/**
 *
 */
public final class RemoteNodeWrapperFactory implements WrapperFactory<NodeServer, RemoteNode> {

  @Override
  public RemoteNode create(NodeServer target, Activator activator) {
    return new RemoteNodeWrapper(target, activator);
  }

  private final class RemoteNodeWrapper implements RemoteNode {

    private final LocalNode server;
    private final RemoteJobService jobServiceProxy;
    private final RemoteTaskService taskServiceProxy;

    private RemoteNodeWrapper(LocalNode server, Activator activator) {
      this.server = server;
      try {
        this.jobServiceProxy = activator.bind(RemoteJobService.class)
            .to(server.getJobService())
            .wrappedBy(new JobServiceWrapperFactory());

        this.taskServiceProxy = activator.bind(RemoteTaskService.class)
            .to(server.getTaskService())
            .wrappedBy(new TaskServiceWrapperFactory());

      } catch (ExportException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public NodeIdentifier getIdentifier() throws RemoteException {
      return server.getId();
    }

    @Override
    public RemoteJobService getJobService() throws RemoteException {
      return this.jobServiceProxy;
    }

    @Override
    public RemoteTaskService getTaskService() throws RemoteException {
      return this.taskServiceProxy;
    }
  }
}
