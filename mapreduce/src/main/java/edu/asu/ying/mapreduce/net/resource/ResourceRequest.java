package edu.asu.ying.mapreduce.net.resource;

import com.google.common.base.Preconditions;

import java.net.URISyntaxException;


/**
 * A {@link ResourceRequest} indicates to a remote node that we would like a remote reference to one of its
 * resources.
 */
public final class ResourceRequest
	extends ResourceMessage
{
	private static final long SerialVersionUID = 1L;

	public static ResourceRequest locatedBy(final ResourceIdentifier uri)
			throws URISyntaxException {

		return new ResourceRequest(uri);
	}

	/**
	 * Initializes the message with a given {@link edu.asu.ying.mapreduce.net.resource.RemoteResource} URI.
	 * @param resourceUri the fully formed URI of the resource. {@see RemoteResource} for formatting details.
	 */
	private ResourceRequest(final ResourceIdentifier resourceUri) throws URISyntaxException {
		Preconditions.checkNotNull(resourceUri);
		// Set the destination URI from only the host and port of the resource URI
		this.setDestinationUri(new ResourceIdentifier(resourceUri.getScheme(), resourceUri.getAddress()));

		if (!ResourceMessage.isValidResourceUri(resourceUri)) {
			throw new URISyntaxException(resourceUri.toString(), "Not a valid ResourceMessage URI", 0);
		}
		this.setResourceUri(resourceUri);
	}
}
