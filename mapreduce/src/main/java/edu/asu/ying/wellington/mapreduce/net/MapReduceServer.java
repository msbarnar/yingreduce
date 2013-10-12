package edu.asu.ying.wellington.mapreduce.net;

import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.List;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskServer;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 * {@code MapReduceServer} is the layer between the network and the mapreduce services. The server
 * implements the {@link LocalNode} and {@link RemoteNode} interfaces.
 */
public final class MapReduceServer implements LocalNode {

  // Network layer
  private final LocalPeer localPeer;
  // Exported to network
  private final RemoteNode remoteNode;

  // Service layer
  private final JobService jobService;
  private final TaskService taskService;


  public MapReduceServer(final LocalPeer localPeer) {
    this.localPeer = localPeer;
    this.jobService = new JobServer(this);
    this.taskService = new TaskServer(this);

    try {
      this.remoteNode = this.localPeer.getActivator()
          .bind(RemoteNode.class)
          .to(this)
          .wrappedBy(MapReduceServerWrapper.class);
    } catch (ExportException e) {
      throw new RuntimeException("Failed to export remote node reference", e);
    }
  }

  @Override
  public NodeIdentifier getIdentifier() {
    return null;
  }

  @Override
  public RemoteNode getAsRemote() {
    return null;
  }

  @Override
  public RemoteNode findNode(String searchKey) {
    return null;
  }

  @Override
  public List<RemoteNode> findNodes(String searchKey) {
    return null;
  }

  @Override
  public JobService getJobService() {
    return this.jobService;
  }

  @Override
  public TaskService getTaskService() {
    return this.taskService;
  }

  public final class MapReduceServerWrapper implements RemoteNode {

    private final MapReduceServer server;
    private final RemoteJobService jobServiceProxy;
    private final RemoteTaskService taskServiceProxy;

    private MapReduceServerWrapper(MapReduceServer server, Activator activator) {
      this.server = server;
      try {
        JobService jobService = server.getJobService();
        this.jobServiceProxy = activator.bind(RemoteJobService.class).to(jobService)
            .wrappedBy(jobService.getWrapper());

        TaskService taskService = server.getTaskService();
        this.taskServiceProxy = activator.bind(RemoteTaskService.class).to(taskService)
            .wrappedBy(taskService.getWrapper());
      } catch (ExportException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public NodeIdentifier getIdentifier() throws RemoteException {
      return server.getIdentifier();
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
