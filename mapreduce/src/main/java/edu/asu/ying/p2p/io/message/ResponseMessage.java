package edu.asu.ying.p2p.io.message;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.annotation.Nullable;

import edu.asu.ying.p2p.PeerIdentifier;

/**
 *
 */
public class ResponseMessage extends MessageBase {

  private static final long SerialVersionUID = 1L;

  private static final class Property {

    private static final String Content = "response.content";
  }

  public static ResponseMessage inResponseTo(final Message request) {
    return new ResponseMessage(request.getId(), request.getTag(), request.getSourceNode());
  }

  private ResponseMessage(final String id, final String tag, final PeerIdentifier destinationNode) {
    super(id, tag, destinationNode);
  }

  public final void setContent(final Serializable content) {
    this.properties.put(Property.Content, Preconditions.checkNotNull(content));
  }

  @Nullable
  public final Serializable getContent() {
    return this.properties.getDynamicCast(Property.Content, Serializable.class);
  }
}
