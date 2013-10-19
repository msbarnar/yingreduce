package edu.asu.ying.wellington.mapreduce.server;

import java.io.IOException;
import java.util.List;

/**
 * Facilitates the finding of other nodes in the network.
 */
public interface NodeLocator {

  /**
   * Returns the node whose name most closely matches {@code name}.
   */
  RemoteNode find(String name) throws IOException;

  /**
   * Returns at least {@code 0} and at most {@code count} nodes.
   * <p/>
   * The actual number returned is dependant on the underlying network.
   */
  List<RemoteNode> find(String name, int count) throws IOException;

  /**
   * Returns immediately connected nodes. The number of nodes returned is dependent on the
   * underlying network.
   */
  List<RemoteNode> neighbors();
}
