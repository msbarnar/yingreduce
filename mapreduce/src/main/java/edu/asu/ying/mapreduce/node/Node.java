package edu.asu.ying.mapreduce.node;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.node.rmi.NodeProxy;

/**
 * {@code Node} provides the interface common to local and remote nodes.
 */
public interface Node {

  Scheduler getScheduler() throws RemoteException;

  NodeURI getNodeURI() throws RemoteException;
}
