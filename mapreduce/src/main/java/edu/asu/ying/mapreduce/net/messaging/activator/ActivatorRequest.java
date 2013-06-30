package edu.asu.ying.mapreduce.net.messaging.activator;

import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.MessageBase;


/**
 * A {@link ActivatorRequest} indicates to a remote node that we would like a remote reference to
 * its {@link edu.asu.ying.mapreduce.rmi.Activator}.
 */
public final class ActivatorRequest extends MessageBase {

  private static final long SerialVersionUID = 1L;

  public ActivatorRequest(final NodeURI destinationUri) {
    super(destinationUri);
  }
}
