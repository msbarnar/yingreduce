package edu.asu.ying.p2p;

import java.io.IOException;
import java.rmi.UnknownHostException;
import java.util.List;

import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.node.NodeURL;
import edu.asu.ying.p2p.rmi.RMIActivator;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode {

  void join(final NodeURL bootstrap) throws IOException;

  List<RemoteNode> getNeighbors();

  RMIActivator getActivator();

  Scheduler getScheduler();

  RemoteNode findNode(final NodeIdentifier uri) throws UnknownHostException;

  NodeIdentifier getIdentifier();
}
