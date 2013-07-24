package edu.asu.ying.mapreduce.net;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;

/**
 * {@code Node} provides the interface common to local and remote nodes.
 */
public interface Node {

  Scheduler getScheduler();

  NodeURI getNodeURI();
}
