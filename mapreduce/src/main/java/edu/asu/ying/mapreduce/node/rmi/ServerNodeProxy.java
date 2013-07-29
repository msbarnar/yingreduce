package edu.asu.ying.mapreduce.node.rmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.node.LocalNode;
import edu.asu.ying.mapreduce.node.Node;
import edu.asu.ying.mapreduce.node.NodeURI;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;

/**
 * {@code ServerNodeProxy} is the server-side implementation of a remote node proxy allowing remote
 * access to local resource providers, e.g. the task scheduler.
 */
public final class ServerNodeProxy implements RemoteNodeProxy {

  private static final long SerialVersionUID = 1L;

  /**
   * Creates a proxy to the specified {@link LocalNode}, exporting its objects as
   * {@link java.rmi.Remote} references for remote consumption.
   *
   * @return A {@link java.rmi.Remote} reference to the {@link LocalNode} suitable for transmission
   * to and use by remote nodes.
   */
  public static RemoteNodeProxy createProxyTo(final LocalNode localNode) throws RemoteException {
    final RemoteNodeProxy instance = new ServerNodeProxy(localNode);

    return localNode.getActivator().export(instance, null);
  }

  private final LocalNode localNode;
  private final Scheduler scheduler;
  private final NodeURI nodeURI;

  private ServerNodeProxy(final LocalNode localNode) throws RemoteException {
    this.localNode = localNode;
    this.scheduler = localNode.getScheduler();
    this.nodeURI = localNode.getNodeURI();
  }

  @Override
  public Scheduler getScheduler() throws RemoteException {
    return this.localNode.getActivator().export(this.scheduler, null);
  }

  @Override
  public NodeURI getNodeURI() throws RemoteException {
    return this.nodeURI;
  }
}
