package edu.asu.ying.p2p.message;

import java.io.Serializable;

import javax.annotation.Nullable;

/**
 *
 */
public class ResponseMessage extends MessageBase {

  private static final long serialVersionUID = 1L;

  protected Serializable content;

  public static ResponseMessage inResponseTo(Message request) {
    return new ResponseMessage(request.getId(), request.getTag(), request.getSender());
  }

  protected ResponseMessage(String id, String tag, String destination) {
    super(id, tag, destination);
  }

  public void setContent(@Nullable Serializable content) {
    this.content = content;
  }

  @Nullable
  public Serializable getContent() {
    return this.content;
  }
}
