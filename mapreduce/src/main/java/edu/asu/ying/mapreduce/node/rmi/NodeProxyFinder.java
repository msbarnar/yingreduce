package edu.asu.ying.mapreduce.node.rmi;

import edu.asu.ying.mapreduce.node.NodeURI;
import edu.asu.ying.mapreduce.node.NodeURL;

/**
 * {@code NodeProxyFinder} provides {@link java.rmi.Remote} proxies to {@link RemoteNodeProxy} objects
 * given the node's key or address.
 */
public interface NodeProxyFinder {

  RemoteNodeProxy findNode(final NodeURI uri);
  RemoteNodeProxy findNode(final NodeURL url);
}
