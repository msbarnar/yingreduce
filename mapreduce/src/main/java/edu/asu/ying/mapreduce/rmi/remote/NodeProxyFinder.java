package edu.asu.ying.mapreduce.rmi.remote;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.NodeURL;

/**
 * {@code NodeProxyFinder} provides {@link java.rmi.Remote} proxies to {@link NodeProxy} objects
 * given the node's key or address.
 */
public interface NodeProxyFinder {

  NodeProxy findNode(final NodeURI uri);
  NodeProxy findNode(final NodeURL url);
}
