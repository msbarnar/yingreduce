package edu.asu.ying.mapreduce.net.resource;

import edu.asu.ying.mapreduce.messaging.MessageBase;

import javax.annotation.Nullable;
import java.net.URISyntaxException;


/**
 * A {@code ResourceMessage} is the base message referencing a {@link RemoteResource}.
 * </p>
 * The following properties are defined on this message:
 * <ul>
 *     {@code resource.uri} - the full resource URI, including address.
 *     {@code resource.type.name} - the name of the type of resource referenced.
 *     {@code resource.instance.name} - (optional) the name of the specific instance of the resource.
 * </ul>
*/
public abstract class ResourceMessage
		extends MessageBase
{
	public static boolean isValidResourceUri(final ResourceIdentifier uri) {
		return uri.getScheme().toLowerCase().equals(Property.Scheme);
	}

	/**
	 * Defines the keys of the properties defined by this message.
	 */
	public static final class Property {
		public static final String Scheme = "resource";
		public static final String ResourceUri = "resource.uri";
		public static final String ResourceTypeName = "resource.type.name";
		public static final String ResourceName = "resource.instance.name";
	}

	private static final long SerialVersionUID = 1L;

	public ResourceMessage() {
	}
	public ResourceMessage(final ResourceIdentifier destinationUri) {
		super(destinationUri);
	}

	public final void setResourceUri(final ResourceIdentifier uri) {
		this.properties.put(Property.ResourceUri, uri);
		this.setResourceTypeName(uri.getPath());
		this.setResourceName(uri.getName());
	}
	public final @Nullable ResourceIdentifier getResourceUri() {
		return this.properties.getDynamicCast(Property.ResourceUri, ResourceIdentifier.class);
	}

	protected final void setResourceTypeName(final String typeName) {
		this.properties.put(Property.ResourceTypeName, typeName);
	}
	public final String getResourceTypeName() {
		return this.properties.getNullAsEmpty(Property.ResourceTypeName);
	}

	protected final void setResourceName(final String name) {
		this.properties.put(Property.ResourceName, name);
	}
	public final String getResourceName() {
		return this.properties.getNullAsEmpty(Property.ResourceName);
	}
}
