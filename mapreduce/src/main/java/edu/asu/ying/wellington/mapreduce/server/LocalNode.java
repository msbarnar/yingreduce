package edu.asu.ying.wellington.mapreduce.server;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface LocalNode {

  NodeIdentifier getID();

  RemoteNode getAsRemote();

  RemoteNode findNode(String searchKey) throws IOException;

  List<RemoteNode> findNodes(String searchKey, int count) throws IOException;

  List<RemoteNode> getNeighbors();
}
