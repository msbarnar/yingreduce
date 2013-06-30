package edu.asu.ying.mapreduce.net.resource;

import java.net.URISyntaxException;


/**
 * A {@link ActivatorRequest} indicates to a remote node that we would like a remote reference to
 * its {@link edu.asu.ying.mapreduce.rmi.Activator}.
 */
public final class ActivatorRequest
    extends MessageBase {

  private static final long SerialVersionUID = 1L;

  public static ActivatorRequest locatedBy(final ResourceIdentifier uri)
      throws URISyntaxException {

    return new ActivatorRequest(uri);
  }

  /**
   * Initializes the message with a given {@link edu.asu.ying.mapreduce.net.resource.RemoteResource}
   * URI.
   *
   * @param resourceUri the fully formed URI of the resource. {@see RemoteResource} for formatting
   *                    details.
   */
  private ActivatorRequest(final ResourceIdentifier resourceUri) throws URISyntaxException {
    // Set the destination URI from only the host and port of the resource URI
    super(resourceUri);

    if (!ResourceMessage.isValidResourceUri(resourceUri)) {
      throw new URISyntaxException(resourceUri.toString(), "Not a valid ResourceMessage URI", 0);
    }
  }
}
