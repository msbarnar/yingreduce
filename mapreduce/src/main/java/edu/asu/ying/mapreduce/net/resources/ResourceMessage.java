package edu.asu.ying.mapreduce.net.resources;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.net.messaging.MessageBase;


/**
 * A {@code ResourceMessage} is the base message referencing a {@link RemoteResource}. </p> The
 * following properties are defined on this message: <ul> {@code resources.uri} - the full resources
 * URI, including address. {@code resources.type.name} - the name of the type of resources
 * referenced. {@code resources.instance.name} - (optional) the name of the specific instance of the
 * resources. </ul>
 */
public abstract class ResourceMessage
    extends MessageBase {

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

  public final
  @Nullable
  ResourceIdentifier getResourceUri() {
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
