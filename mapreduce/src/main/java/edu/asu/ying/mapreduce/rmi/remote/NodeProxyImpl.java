package edu.asu.ying.mapreduce.rmi.remote;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.net.LocalNode;
import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;

/**
 * {@code NodeProxyImpl} is the server-side implementation of a remote node proxy allowing remote
 * access to local resource providers, e.g. the task scheduler.
 */
public final class NodeProxyImpl implements NodeProxy {

  /**
   * Creates a proxy to the specified {@link LocalNode}, exporting its objects as
   * {@link java.rmi.Remote} references for remote consumption.
   *
   * @return A {@link java.rmi.Remote} reference to the {@link LocalNode} suitable for transmission
   * to and use by remote nodes.
   */
  public static NodeProxy createProxyTo(final LocalNode localNode) throws RemoteException {
    return new NodeProxyImpl(localNode.getActivator().createReference(localNode.getScheduler(),
                                                                      null));
  }

  private final Scheduler scheduler;

  private NodeProxyImpl(final Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public Scheduler getScheduler() throws RemoteException {
    return null;
  }

  @Override
  public NodeURI getNodeURI() throws RemoteException {
    return null;
  }
}
