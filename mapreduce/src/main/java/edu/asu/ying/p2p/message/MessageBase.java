package edu.asu.ying.p2p.message;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.rmi.RemoteException;
import java.util.UUID;

import javax.annotation.Nullable;

import edu.asu.ying.p2p.PeerName;


/**
 * Base class for a basic {@link Message}. <p> The following properties are defined on this message:
 * <ul> <li>{@code message.id} - the universally unique identifier of this message.</li> <li>{@code
 * message.uri.destination} - the URI of the node for which this message is destined.</li>
 * <li>{@code message.uri.sender} - the URI of the node from which this node originated.</li>
 * <li>{@code message.exception} - an exception, if one was thrown.</li> </ul>
 */
public abstract class MessageBase
    implements Message {

  private static final long SerialVersionUID = 1L;

  protected String id;
  protected String tag;
  protected PeerName destination;
  protected PeerName sender;
  protected Throwable exception;

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

  public MessageBase(final String tag, final PeerName destination) {
    this.setTag(tag);
    this.setDestination(destination);
  }

  public MessageBase(final String id, final String tag, final PeerName destination) {
    this.setId(id);
    this.setTag(tag);
    this.setDestination(destination);
  }

  /*
   * Accessors
   */

  /**
   * Initializes the message ID with a random {@link UUID}.
   */
  public final void setId() {
    this.setId(UUID.randomUUID().toString());
  }

  public final void setId(final String id) {
    this.id = Preconditions.checkNotNull(Strings.emptyToNull(id));
  }

  public final void setId(final UUID id) {
    Preconditions.checkNotNull(id);
    this.setId(id.toString());
  }

  @Override
  public final String getId() {
    return this.id;
  }

  @Override
  public final String getTag() {
    return this.tag;
  }

  public final void setTag(final String tag) {
    this.tag = Preconditions.checkNotNull(Strings.emptyToNull(tag));
  }

  public final void setSender(final PeerName sender) {
    this.sender = Preconditions.checkNotNull(sender);
  }

  @Override
  public final PeerName getSender() {
    return this.sender;
  }

  public final void setDestination(final PeerName destination) {
    this.destination = Preconditions.checkNotNull(destination);
  }

  public final PeerName getDestination() {
    return this.destination;
  }

  public final void setException(final Throwable exception) {
    this.exception = exception;
  }

  /**
   * Gets a {@link RemoteException} wrapping the underlying cause from the remote host.
   */
  @Nullable
  public final RemoteException getException() {
    if (this.exception != null) {
      return new RemoteException("Remote node threw an exception", this.exception);
    } else {
      return null;
    }
  }

  public final void checkException() throws RemoteException {
    final RemoteException ex = this.getException();
    if (ex != null) {
      throw ex;
    }
  }

  /**
   * Makes this message into a response to {@code request}.
   *
   * @param request the message to which to respond.
   * @return this message.
   */
  protected final Message makeResponseTo(final Message request) {
    this.setDestination(request.getSender());
    this.setId(request.getId());
    return this;
  }
}