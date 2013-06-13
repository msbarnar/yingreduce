package edu.asu.ying.mapreduce.rmi.finder.kad;

import edu.asu.ying.mapreduce.messaging.MessageBase;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * A {@link GetResourceMessage} indicates to a remote node that we would like access to a resource it has.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     <li>{@code destination-uri}: </li>
 * </ul>
 */
public class GetResourceMessage
	extends MessageBase
{
	public GetResourceMessage(final URI resourceUri) throws URISyntaxException {
		// Set the destination URI from only the host and port of the resource URI
		super(new URI(null, null, resourceUri.getHost(), resourceUri.getPort(), null, null, null));
	}
}
