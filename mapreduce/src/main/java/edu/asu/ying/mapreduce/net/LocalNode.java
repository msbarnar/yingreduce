package edu.asu.ying.mapreduce.net;

import java.io.IOException;
import java.util.List;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalNode extends Node {

  void join(final NodeURL bootstrap) throws IOException;

  List<RemoteNode> getNeighbors();
}
