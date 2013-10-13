package edu.asu.ying.wellington.mapreduce.net;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.rmi.Activator;
import edu.asu.ying.p2p.rmi.WrapperFactory;
import edu.asu.ying.wellington.mapreduce.job.JobServer;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskServer;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 * {@code NodeServer} is the layer between the network and the mapreduce services. The server
 * implements the {@link LocalNode} and {@link RemoteNode} interfaces.
 */
public final class NodeServer implements LocalNode {

  // Network layer
  private final LocalPeer localPeer;
  // Exported to network
  private final RemoteNode remoteNode;

  // Service layer
  private final NodeIdentifier identifier;
  private final JobService jobService;
  private final TaskService taskService;


  public NodeServer(final LocalPeer localPeer) {
    this.localPeer = localPeer;
    this.identifier = new NodeIdentifier(localPeer.getIdentifier().toString());
    this.jobService = new JobServer(this);
    this.taskService = new TaskServer(this);

    try {
      this.remoteNode = this.localPeer.getActivator()
          .bind(RemoteNode.class)
          .to(this)
          .wrappedBy(new MapReduceServerWrapperFactory());
    } catch (ExportException e) {
      throw new RuntimeException("Failed to export remote node reference", e);
    }
  }

  @Override
  public NodeIdentifier getIdentifier() {
    return this.identifier;
  }

  @Override
  public RemoteNode getAsRemote() {
    return this.remoteNode;
  }

  @Override
  public RemoteNode findNode(String searchKey) throws IOException {
    return this.localPeer.findPeer(searchKey).getReference(RemoteNode.class);
  }

  @Override
  public List<RemoteNode> findNodes(String searchKey, int count) throws IOException {
    List<RemoteNode> nodes = new ArrayList<>();
    for (RemotePeer peer : this.localPeer.findPeers(searchKey, count)) {
      try {
        nodes.add(peer.getReference(RemoteNode.class));
      } catch (RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
    return nodes;
  }

  @Override
  public List<RemoteNode> getNeighbors() {
    List<RemoteNode> neighbors = new ArrayList<>();
    for (RemotePeer peer : this.localPeer.getNeighbors()) {
      try {
        neighbors.add(peer.getReference(RemoteNode.class));
      } catch (RemoteException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
    return neighbors;
  }

  @Override
  public JobService getJobService() {
    return this.jobService;
  }

  @Override
  public TaskService getTaskService() {
    return this.taskService;
  }

  public final class MapReduceServerWrapperFactory implements
                                                   WrapperFactory<NodeServer, RemoteNode> {

    @Override
    public RemoteNode create(NodeServer target, Activator activator) {
      return new MapReduceServerWrapper(target, activator);
    }

    private final class MapReduceServerWrapper implements RemoteNode {

      private final NodeServer server;
      private final RemoteJobService jobServiceProxy;
      private final RemoteTaskService taskServiceProxy;

      private MapReduceServerWrapper(NodeServer server, Activator activator) {
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
}
