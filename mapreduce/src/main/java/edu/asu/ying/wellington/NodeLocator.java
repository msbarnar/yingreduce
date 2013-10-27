package edu.asu.ying.wellington;

import java.io.IOException;
import java.util.List;

/**
 * Facilitates the finding of other nodes in the network.
 */
public interface NodeLocator {

  /**
   * Returns this node as a remote proxy.
   */
  RemoteNode local();

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
   * Returns the node that is the {@code distance}th node from the search string, or if
   * {@code distance} is greater than the number of nodes found, returns the most distant
   * node found.
   */
  RemoteNode findByDistance(String name, int distance) throws IOException;

  /**
   * Returns immediately connected nodes. The number of nodes returned is dependent on the
   * underlying network.
   */
  List<RemoteNode> neighbors();
}
