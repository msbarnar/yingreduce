package edu.asu.ying.mapreduce.net;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nullable;

/**
 * A {@code NodeURL} identifies the exact location of and means of reaching a specific node, in
 * addition to uniquely identifying the node per its {@link NodeURI}.
 */
public interface NodeURL extends NodeURI {

  @Nullable
  InetAddress getAddress();

  URI toURI() throws URISyntaxException;
}
