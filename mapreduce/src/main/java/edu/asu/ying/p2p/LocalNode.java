package edu.asu.ying.p2p;

import java.io.IOException;
import java.rmi.UnknownHostException;
import java.util.List;

import edu.asu.ying.mapreduce.node.NodeURL;
import edu.asu.ying.p2p.rmi.NodeProxy;
import edu.asu.ying.p2p.rmi.ServerActivator;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode {

  void join(final NodeURL bootstrap) throws IOException;

  List<NodeProxy> getNeighbors();

  ServerActivator getActivator();

  NodeProxy findNode(final NodeIdentifier uri) throws UnknownHostException;
}
