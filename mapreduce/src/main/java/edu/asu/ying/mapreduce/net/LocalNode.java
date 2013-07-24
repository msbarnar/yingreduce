package edu.asu.ying.mapreduce.net;

import java.io.IOException;
import java.util.List;

import edu.asu.ying.mapreduce.rmi.remote.NodeProxy;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode extends Node {

  void join(final NodeURL bootstrap) throws IOException;

  List<NodeProxy> getNeighbors();
}
