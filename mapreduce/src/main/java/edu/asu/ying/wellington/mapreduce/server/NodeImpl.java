package edu.asu.ying.wellington.mapreduce.server;

import com.google.inject.Inject;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.RemotePeer;

/**
 * {@code NodeImpl} forms a network of mapreduce services overlayed on the P2P network. The network
 * supports finding other nodes and obtaining remote access to their services.
 */
public final class NodeImpl implements LocalNode, NodeLocator {

  private final RemoteNode proxy;

  // Network layer
  private final LocalPeer localPeer;

  // Derived from network
  private final NodeIdentifier identifier;


  @Inject
  private NodeImpl(LocalPeer localPeer,
                   NodeExporter exporter) {

    this.localPeer = localPeer;
    // Use the same node identifier as the underlying P2P node
    this.identifier = NodeIdentifier.forString(localPeer.getIdentifier().toString());

    try {
      this.proxy = exporter.export(this);
    } catch (ExportException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public NodeIdentifier getID() {
    return identifier;
  }

  @Override
  public RemoteNode find(String searchKey) throws IOException {
    return localPeer.findPeer(searchKey).getReference(RemoteNode.class);
  }

  @Override
  public List<RemoteNode> find(String searchKey, int count) throws IOException {
    List<RemoteNode> nodes = new ArrayList<>();
    for (RemotePeer peer : localPeer.findPeers(searchKey, count)) {
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