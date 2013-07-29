package edu.asu.ying.mapreduce.node.io.message;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.node.NodeURI;

/**
 *
 */
public class ResponseMessage extends MessageBase {

  private static final long SerialVersionUID = 1L;

  private static final class Property {
    private static final String Content = "response.content";
  }

  public static ResponseMessage inResponseTo(final Message request) {
    return new ResponseMessage(request.getSourceNode());
  }

  private ResponseMessage(final NodeURI destinationNode) {
    super(destinationNode);
  }

  public final void setContent(final Serializable content) {
    this.properties.put(Property.Content, Preconditions.checkNotNull(content));
  }

  @Nullable
  public final Serializable getContent() {
    return this.properties.getDynamicCast(Property.Content, Serializable.class);
  }
}
