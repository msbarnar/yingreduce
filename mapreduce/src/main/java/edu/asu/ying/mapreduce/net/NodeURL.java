package edu.asu.ying.mapreduce.net;

import java.net.InetAddress;

import javax.annotation.Nullable;

/**
 * {@code NodeURL} specifies the location of a specific node in the network.
 */
public interface NodeURL extends NodeURI {

  NodeURI getNodeId();

  @Nullable
  InetAddress getAddress();
}
