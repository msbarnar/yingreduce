package edu.asu.ying.wellington.mapreduce.server;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface NodeLocator {

  RemoteNode find(String searchKey) throws IOException;

  List<RemoteNode> find(String searchKey, int count) throws IOException;

  List<RemoteNode> neighbors();
}
