package edu.asu.ying.mapreduce.rmi.resource;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import edu.asu.ying.mapreduce.messaging.MessageBase;

import java.io.Serializable;
import java.net.URISyntaxException;


/**
 * A {@link GetResourceMessage} indicates to a remote node that we would like a remote reference to one of its
 * resources.
 * <p>
 * The following properties are defined by this message:
 * <ul>
 *     {@code resource-uri} - the full resource URI.
 *     {@code resource-typename} - the name of the type of the resource. This can be a class name or arbitrary identifier.
 *     {@code resource-name} - (nullable) the unique name of the resource, if it has one.
 * </ul>
 */
public class GetResourceMessage
	extends MessageBase
{
	/**
	 * Initializes the message with a given {@link edu.asu.ying.mapreduce.rmi.resource.RemoteResource} URI.
	 * @param resourceUri the fully formed URI of the resource. {@see RemoteResource} for formatting details.
	 */
	public GetResourceMessage(final ResourceIdentifier resourceUri) throws URISyntaxException {
		// Set the destination URI from only the host and port of the resource URI
		super(new ResourceIdentifier("node", resourceUri.getHost(), resourceUri.getPort()));

		if (!resourceUri.getScheme().toLowerCase().equals("resource")) {
			throw new URISyntaxException(resourceUri.toString(), "GetResource URI scheme part must be 'resource'", 0);
		}
		this.setResourceUri(resourceUri);
	}

	public final void setResourceUri(final ResourceIdentifier uri) {
		this.properties.put("resource-uri", uri);
		this.properties.put("resource-typename", uri.getPath());
		this.properties.put("resource-name", uri.getName());
	}
	public final String getResourceType() {
		final Optional<Serializable> typeName = Optional.fromNullable(this.properties.get("resource-typename"));
		if (typeName.isPresent()) {
			return Strings.emptyToNull(String.valueOf(typeName));
		} else {
			return null;
		}
	}
	public final String getResourceName() {
		final Optional<Serializable> name = Optional.fromNullable(this.properties.get("resource-name"));
		if (name.isPresent()) {
			return Strings.emptyToNull(String.valueOf(name));
		} else {
			return null;
		}
	}
}
