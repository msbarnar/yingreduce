package edu.asu.ying.mapreduce.rmi.finder.kad;

import edu.asu.ying.mapreduce.messaging.MessageBase;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A {@link GetResourceMessage} indicates to a remote node that we would like a remote reference to one of its resources.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     <li>{@code destination-uri}: </li>
 * </ul>
 */
public class GetResourceMessage
	extends MessageBase
{
	// Identifies the resource we want and host we want it from
	private final URI resourceUri;

	/**
	 * Initializes the message with a given {@link edu.asu.ying.mapreduce.net.RemoteResource} URI.
	 * @param resourceUri the fully formed URI of the resource. {@see RemoteResource} for formatting details.
	 * @throws URISyntaxException
	 */
	public GetResourceMessage(final URI resourceUri) throws URISyntaxException {
		// Set the destination URI from only the host and port of the resource URI
		super(new URI(null, null, resourceUri.getHost(), resourceUri.getPort(), null, null, null));

		if (!resourceUri.getScheme().toLowerCase().equals("resource")) {
			throw new URISyntaxException(resourceUri.toString(), "GetResource URI scheme part must be 'resource'");
		}
		this.resourceUri = resourceUri;
	}

	/**
	 * Initializes the message with the URI of a remote host and the type name of the resource to be retrieved.
	 * @param destinationUri the URI of the remote host to which the request will be sent.
	 * @param resourceType the type name of the resource to retrieve.
	 * @throws URISyntaxException if {@code resourceType} cannot be formatted in a URI.
	 */
	public GetResourceMessage(final URI destinationUri, final String resourceType) throws URISyntaxException {
		this(new URI("resource", destinationUri.getUserInfo(), destinationUri.getHost(), destinationUri.getPort(),
		             resourceType, null, null));
	}

	/**
	 * Initializes the message with the URI of a remote host and the type and name of a specific resource.
	 * @param destinationUri the URI of the remote host to which the request will be sent.
	 * @param resourceType the type name of the resource to retrieve.
	 * @param resourceName the name of the specific instance of the resource to retrieve.
	 * @throws URISyntaxException if any of the parameters cannot be formatted as a URI.
	 */
	public GetResourceMessage(final URI destinationUri, final String resourceType, final String resourceName)
			throws URISyntaxException {
		this(new URI("resource", destinationUri.getUserInfo(), destinationUri.getHost(), destinationUri.getPort(),
		             resourceType, resourceName, null));
	}
}
