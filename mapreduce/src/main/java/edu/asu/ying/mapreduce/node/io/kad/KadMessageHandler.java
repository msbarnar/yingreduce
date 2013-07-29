package edu.asu.ying.mapreduce.node.io.kad;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.mapreduce.node.io.InvalidContentException;
import edu.asu.ying.mapreduce.node.io.message.ExceptionMessage;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.MessageRequestEvent;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;

public abstract class KadMessageHandler
    implements MessageHandler, il.technion.ewolf.kbr.MessageHandler {

  // The actual network listener
  @Inject private KeybasedRouting inputNode;

  private String tag;

  // Event used to signal other listeners of incoming messages
  private final MessageRequestEvent incomingMessageEvent = new MessageRequestEvent();

  protected KadMessageHandler() {
  }

  /**************************************************
   * MessageHandler
   */
  @Override
  public final void bind(final String tag) {
    this.tag = tag;
    // Bind to the Kademlia message dispatch
    inputNode.register(tag, this);
    // We are now receiving messages from the Kademlia network
  }

  @Override
  public final String getTag() {
    return this.tag;
  }

  protected abstract void processMessage(final Message message);
  protected abstract Message processRequest(final Message request);

  /**************************************************
   * il.technion.ewolf.kbr.MessageHandler
   */
  @Override
  public void onIncomingMessage(final Node from, final String tag, final Serializable content) {
    try {
      if (!(content instanceof Message)) {
        throw new InvalidContentException();
      }
      this.processMessage((Message) content);
    } catch (final IOException e) {
      // TODO: logging
      e.printStackTrace();
    }
  }

  @Override
  public Serializable onIncomingRequest(final Node from, final String tag,
                                        final Serializable content) {
    if (!(content instanceof Message)) {
      return new ExceptionMessage(new InvalidContentException());
    }
    return this.processRequest((Message) content);
  }
}
