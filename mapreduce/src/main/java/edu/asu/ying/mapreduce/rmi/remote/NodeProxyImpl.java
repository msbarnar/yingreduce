package edu.asu.ying.mapreduce.rmi.remote;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;

/**
 * {@code NodeProxyImpl} is the server-side implementation of a remote node proxy allowing remote
 * access to local resource providers, e.g. the task scheduler.
 */
public class NodeProxyImpl implements NodeProxy {

  @Override
  public Scheduler getScheduler() throws RemoteException {
    return null;
  }

  @Override
  public NodeURI getNodeURI() throws RemoteException {
    return null;
  }
}
