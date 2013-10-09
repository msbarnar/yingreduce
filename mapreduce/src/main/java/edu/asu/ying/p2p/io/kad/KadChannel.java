package edu.asu.ying.p2p.io.kad;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.asu.ying.p2p.io.Channel;
import edu.asu.ying.p2p.io.InvalidContentException;
import edu.asu.ying.p2p.io.MessageHandler;
import edu.asu.ying.p2p.io.MessageOutputStream;
import edu.asu.ying.p2p.io.UnhandledRequestException;
import edu.asu.ying.p2p.io.message.ExceptionMessage;
import edu.asu.ying.p2p.io.message.Message;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;

/**
 * {@code KadChannel} encompasses the {@link MessageHandler} and {@link MessageOutputStream} tied to
 * the underlying Kademlia network. The {@code KadChannel} provides a single point of access for
 * input from and output to the network.
 */
public final class KadChannel implements Channel, il.technion.ewolf.kbr.MessageHandler {

  // Send/Receive data to/from this Kademlia endpoint
  private final KeybasedRouting kbrNode;

  // Transforms messages to serialized packets
  private final MessageOutputStream sendStream;
  // Receives messages for particular tags
  private final Map<String, MessageHandler> messageHandlers = new HashMap<>();

  public KadChannel(final KeybasedRouting kbrNode) {
    this.kbrNode = kbrNode;
    this.sendStream = new KadSendMessageStream(kbrNode);
  }

  /**
   * Binds a {@link MessageHandler} to messages signed with a particular {@code tag}.
   */
  public final void registerMessageHandler(final MessageHandler handler, final String tag) {
    this.kbrNode.register(tag, this);
    this.messageHandlers.put(tag, handler);
  }

  /**
   * Gets a {@link MessageOutputStream} capable of writing messages to the Kademlia network.
   */
  public final MessageOutputStream getMessageOutputStream() {
    return this.sendStream;
  }

  /**
   * Receives deserialized messages from the Kademlia node and passes them to the bound {@link
   * MessageHandler}.
   *
   * @param from    the Kademlia node that send the message.
   * @param tag     the tag with which the message is signed.
   * @param content the message.
   */
  public final void onIncomingMessage(final Node from, final String tag,
                                      final Serializable content) {

    if (!(content instanceof Message)) {
      // TODO: Logging
      System.out.println("Invalid content from the Kademlia network");
      return;
    }

    // Look up the message handler associated with that tag and pass the message along.
    final MessageHandler handler = this.messageHandlers.get(tag);
    if (handler != null) {
      handler.onIncomingMessage((Message) content);
    } else {
      // TODO: Logging
      System.out.println("Unhandled message from Kademlia network");
    }
  }

  /**
   * Receives a deserialized message from the Kademlia node, passes it to the bound {@link
   * MessageHandler}, and returns a response.
   *
   * @param from    the Kademlia node that send the message.
   * @param tag     the tag with which the message is signed.
   * @param content the message.
   * @return the response from the {@link MessageHandler}, or an {@link ExceptionMessage} wrapping
   *         the exception if one is thrown.
   */
  public final Serializable onIncomingRequest(final Node from, final String tag,
                                              final Serializable content) {

    if (!(content instanceof Message)) {
      // TODO: Logging
      System.out.println("Invalid content from the Kademlia network");
      return new ExceptionMessage(new InvalidContentException());
    }

    final MessageHandler handler = this.messageHandlers.get(tag);
    if (handler != null) {
      return handler.onIncomingRequest((Message) content);
    } else {
      // TODO: Logging
      System.out.println("Unhandled message from Kademlia network");
      return new ExceptionMessage(new UnhandledRequestException());
    }
  }
}
