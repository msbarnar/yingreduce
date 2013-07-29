package edu.asu.ying.mapreduce.node;

import java.io.IOException;
import java.util.List;

import edu.asu.ying.mapreduce.node.rmi.Activator;
import edu.asu.ying.mapreduce.node.rmi.RemoteNodeProxy;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode extends Node {

  void join(final NodeURL bootstrap) throws IOException;

  List<RemoteNodeProxy> getNeighbors();

  Activator getActivator();
}
