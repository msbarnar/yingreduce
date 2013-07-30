package edu.asu.ying.mapreduce.node;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.util.List;

import edu.asu.ying.mapreduce.node.rmi.Activator;
import edu.asu.ying.mapreduce.node.rmi.NodeProxy;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode extends Node {

  void join(final NodeURL bootstrap) throws IOException;

  List<NodeProxy> getNeighbors();

  Activator getActivator();

  NodeProxy findNode(final NodeURI uri) throws UnknownHostException;
}
