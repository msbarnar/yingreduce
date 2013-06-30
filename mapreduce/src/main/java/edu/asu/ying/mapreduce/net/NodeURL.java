package edu.asu.ying.mapreduce.net;

import java.net.InetAddress;

/**
 * {@code NodeURL} specifies the location of a specific node in the network.
 */
public interface NodeURL extends NodeURI {

  InetAddress getAddress();
}
