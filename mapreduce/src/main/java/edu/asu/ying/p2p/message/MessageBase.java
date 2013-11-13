package edu.asu.ying.p2p.message;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.annotation.Nullable;


/**
 * Base class for a basic {@link Message}. <p> The following properties are defined on this
 * message:
 * <ul> <li>{@code message.id} - the universally unique identifier of this message.</li> <li>{@code
 * message.uri.destination} - the URI of the node for which this message is destined.</li>
 * <li>{@code message.uri.sender} - the URI of the node from which this node originated.</li>
 * <li>{@code message.exception} - an exception, if one was thrown.</li> </ul>
 */
public abstract class MessageBase implements Message {

  private static final long serialVersionUID = 1L;

  protected String id;
  protected String tag;
  protected String destination;
  protected String sender;
  protected Throwable exception;

  /**
   * Initializes the message with a random ID.
   */
  public MessageBase(String tag) {
    setId();
    setTag(tag);
  }

  public MessageBase(String id, String tag, String destination) {
    setId(id);
    setTag(tag);
    setDestination(destination);
  }

  /*
   * Accessors
   */

  /**
   * Initializes the message ID with a random {@link UUID}.
   */
  public void setId() {
    setId(UUID.randomUUID().toString());
  }

  public void setId(String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull(id));
  }

  public void setId(UUID id) {
    Preconditions.checkNotNull(id);
    setId(id.toString());
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = Preconditions.checkNotNull(Strings.emptyToNull(tag));
  }

  public void setSender(String sender) {
    this.sender = Preconditions.checkNotNull(sender);
  }

  @Override
  public String getSender() {
    return sender;
  }

  public void setDestination(String destination) {
    this.destination = Preconditions.checkNotNull(destination);
  }

  public String getDestination() {
    return this.destination;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * Gets a {@link RemoteException} wrapping the underlying cause from the remote host.
   */
  @Nullable
  public RemoteException getException() {
    if (exception != null) {
      return new RemoteException("Remote node threw an exception", exception);
    } else {
      return null;
    }
  }
}