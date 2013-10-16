package edu.asu.ying.wellington.mapreduce.server;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface NodeLocator {

  RemoteNode find(String name) throws IOException;

  List<RemoteNode> find(String name, int count) throws IOException;

  List<RemoteNode> neighbors();
}
