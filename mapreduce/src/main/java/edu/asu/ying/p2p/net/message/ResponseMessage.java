package edu.asu.ying.p2p.net.message;

import java.io.Serializable;

import javax.annotation.Nullable;

import edu.asu.ying.p2p.PeerIdentifier;

/**
 *
 */
public class ResponseMessage extends MessageBase {

  private static final long SerialVersionUID = 1L;

  private Serializable content;

  public static ResponseMessage inResponseTo(final Message request) {
    return new ResponseMessage(request.getId(), request.getTag(), request.getSender());
  }

  private ResponseMessage(final String id, final String tag, final PeerIdentifier destination) {
    super(id, tag, destination);
  }

  public final void setContent(final @Nullable Serializable content) {
    this.content = content;
  }

  @Nullable
  public final Serializable getContent() {
    return this.content;
  }
}
