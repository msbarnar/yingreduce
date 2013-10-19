package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.wellington.dfs.DFSService;
import edu.asu.ying.wellington.mapreduce.job.JobService;
import edu.asu.ying.wellington.mapreduce.task.TaskService;

/**
 * {@code NodeImpl} forms a network of mapreduce services overlayed on the P2P network. The network
 * supports finding other nodes and obtaining remote access to their services.
 */
public final class NodeImpl implements LocalNode, NodeLocator {

  private final RemoteNode proxy;

  // Network layer
  private final LocalPeer localPeer;

  // Derived from network
  private final String name;


  @Inject
  private NodeImpl(LocalPeer localPeer,
                   NodeExporter exporter,
                   JobService jobService,
                   TaskService taskService,
                   DFSService dfsService) {

    this.localPeer = localPeer;
    // Use the same node name as the underlying P2P node
    this.name = localPeer.getName().toString();

    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }

    jobService.start();
    taskService.start();
    dfsService.start();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public RemoteNode find(String name) throws IOException {
    try {
      return localPeer.findPeer(name).getReference(RemoteNode.class);
    } catch (PeerNotFoundException e) {
      throw new NodeNotFoundException(name);
    }
  }

  @Override
  public List<RemoteNode> find(String name, int count) throws IOException {
    List<RemoteNode> nodes = new ArrayList<>();
    for (RemotePeer peer : localPeer.findPeers(name, count)) {
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
  public List<RemoteNode> neighbors() {
    List<RemoteNode> neighbors = new ArrayList<>();
    for (RemotePeer peer : localPeer.getNeighbors()) {
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
  public RemoteNode asRemote() {
    return proxy;
  }
}
