package edu.asu.ying.mapreduce.node.kad;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;

/**
 * KadLocalNodeProxy implements access to the {@link edu.asu.ying.p2p.LocalNode} by remote peers.
 */
public class KadLocalNodeProxy implements RemoteNode {

  public static KadLocalNodeProxy createProxyTo(final LocalNode localNode) {
    return new KadLocalNodeProxy(localNode);
  }

  private final LocalNode localNode;

  private KadLocalNodeProxy(final LocalNode localNode) {
    this.localNode = localNode;
  }

  @Override
  public NodeIdentifier getIdentifier() throws RemoteException {
    return this.localNode.getIdentifier();
  }

  @Override
  public Scheduler getScheduler() throws RemoteException {
    return this.localNode.getScheduler();
  }
}
