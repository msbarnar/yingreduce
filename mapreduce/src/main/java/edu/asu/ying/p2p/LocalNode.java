package edu.asu.ying.p2p;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.p2p.node.NodeURL;
import edu.asu.ying.p2p.rmi.RMIActivator;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode {

  void join(final NodeURL bootstrap) throws IOException;

  Collection<RemoteNode> getNeighbors();

  RMIActivator getActivator();

  LocalScheduler getScheduler();

  RemoteNode findNode(final NodeIdentifier uri) throws UnknownHostException;

  NodeIdentifier getIdentifier();

  RemoteNode getProxy();
}
