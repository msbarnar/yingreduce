package edu.asu.ying.p2p.io.message;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.p2p.NodeIdentifier;


/**
 * Base class for a basic {@link Message}. <p> The following properties are defined on this message:
 * <ul> <li>{@code message.id} - the universally unique identifier of this message.</li> <li>{@code
 * message.uri.destination} - the URI of the node for which this message is destined.</li>
 * <li>{@code message.uri.source} - the URI of the node from which this node originated.</li>
 * <li>{@code message.exception} - an exception, if one was thrown.</li> </ul>
 */
public abstract class MessageBase
    implements Message {

  private static final long SerialVersionUID = 1L;

  /**
   * Defines the keys of the properties defined by this message.
   */
  public static final class Property {

    public static final String MessageId = "message.id";
    public static final String MessageTag = "message.tag";
    public static final String DestinationURI = "message.uri.destination";
    public static final String SourceURI = "message.uri.source";
    public static final String Exception = "exception";
    public static final String Arguments = "arguments";
  }

  protected final Properties properties = new Properties();

	/*
     * Constructors
	 */

  /**
   * Initializes the message with a random ID.
   */
  public MessageBase(final String tag) {
    this.setId();
    this.setTag(tag);
  }

  public MessageBase(final String id, final String tag) {
    this.setId(id);
    this.setTag(tag);
  }

  public MessageBase(final String tag, final NodeIdentifier destinationNode) {
    this.setTag(tag);
    this.setDestinationNode(destinationNode);
  }

  public MessageBase(final String id, final String tag, final NodeIdentifier destinationNode) {
    this.setId(id);
    this.setTag(tag);
    this.setDestinationNode(destinationNode);
  }

  /*
   * Accessors
   */

  /**
   * Initializes the message ID with a random {@link UUID}.
   */
  public void setId() {
    this.setId(UUID.randomUUID().toString());
  }

  /**
   * Initializes the message ID with a string. If {@code id} is null or empty, a random ID will be
   * set.
   */
  public void setId(final String id) {
    // Don't allow empty strings
    if (Strings.isNullOrEmpty(id)) {
      this.setId();
    } else {
      this.properties.put(Property.MessageId, id);
    }
  }

  public void setId(final UUID id) {
    this.setId(id.toString());
  }

  @Override
  public String getId() {
    final
    Optional<Serializable>
        id =
        Optional.fromNullable(this.properties.get(Property.MessageId));
    if (!id.isPresent()) {
      // We can't have no id; set a random one.
      this.setId();
      return this.getId();
    }

    final String szId = String.valueOf(id.get());
    // Don't allow empty ID
    if (szId.isEmpty()) {
      this.setId();
      return this.getId();
    } else {
      return szId;
    }
  }

  @Override
  public String getTag() {
    final
    Optional<Serializable>
        tag =
        Optional.fromNullable(this.properties.get(Property.MessageTag));
    if (!tag.isPresent()) {
      throw new IllegalArgumentException("Message tag cannot be null.");
    }

    final String szTag = String.valueOf(tag.get());
    if (szTag.isEmpty()) {
      throw new IllegalArgumentException("Message tag cannot be empty.");
    } else {
      return szTag;
    }
  }

  public void setTag(final String tag) {
    this.properties.put(Property.MessageTag, Preconditions.checkNotNull(Strings.emptyToNull(tag)));
  }

  @Override
  public Properties getProperties() {
    return this.properties;
  }

  public void setSourceNode(final NodeIdentifier sourceNode) {
    this.properties.put(Property.SourceURI, Preconditions.checkNotNull(sourceNode));
  }

  @Override
  public NodeIdentifier getSourceNode() {
    return this.properties.getDynamicCast(Property.SourceURI, NodeIdentifier.class);
  }

  public void setDestinationNode(final NodeIdentifier destinationNode) {
    this.properties.put(Property.DestinationURI, Preconditions.checkNotNull(destinationNode));
  }

  @Nullable
  public NodeIdentifier getDestinationNode() {
    return this.properties.getDynamicCast(Property.DestinationURI, NodeIdentifier.class);
  }

  public final void setException(final Throwable exception) {
    this.properties.put(Property.Exception, Preconditions.checkNotNull(exception));
  }

  /**
   * Gets a {@link RemoteException} wrapping the underlying cause from the remote host.
   */
  public final
  @Nullable
  RemoteException getException() {
    final
    Optional<Serializable>
        cause = Optional.fromNullable(this.properties.get(Property.Exception));
    if (!cause.isPresent()) {
      return null;
    }
    if (!(cause.get() instanceof Throwable)) {
      return new RemoteException(String.valueOf(cause.get()));
    }

    return new RemoteException("Remote node returned an exception.", (Throwable) cause.get());
  }

  public final void setArguments(final Properties args) {
    Preconditions.checkNotNull(args);
    this.properties.put(Property.Arguments, args);
  }

  public final Properties getArguments() {
    final Properties arguments
        = this.properties.getDynamicCast(Property.Arguments, Properties.class);
    if (arguments == null) {
      return Properties.Empty;
    } else {
      return arguments;
    }
  }

  /**
   * Makes this message into a response to {@code request}.
   *
   * @param request the message to which to respond.
   * @return this message.
   */
  public final Message makeResponseTo(final Message request) {
    Preconditions.checkNotNull(request);
    this.setDestinationNode(request.getSourceNode());
    this.setId(request.getId());
    return this;
  }
}