package edu.asu.ying.mapreduce.rmi.remote;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.MessageBase;


/**
 * A {@link NodeProxyRequest} indicates to a remote node that we would like a remote reference to
 * its {@link edu.asu.ying.mapreduce.rmi.Activator}.
 */
public final class NodeProxyRequest extends MessageBase {

  private static final long SerialVersionUID = 1L;

  public NodeProxyRequest(final NodeURI destinationUri) {
    super(destinationUri);
  }
}
