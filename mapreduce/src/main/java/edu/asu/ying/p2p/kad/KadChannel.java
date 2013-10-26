package edu.asu.ying.p2p.kad;

import com.google.inject.Inject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.asu.ying.p2p.Channel;
import edu.asu.ying.p2p.InvalidContentException;
import edu.asu.ying.p2p.UnhandledRequestException;
import edu.asu.ying.p2p.message.ExceptionMessage;
import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.MessageOutputStream;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;

/**
 * {@code KadChannel} encompasses the {@link MessageHandler} and {@link MessageOutputStream} tied
 * to
 * the underlying Kademlia network. The {@code KadChannel} provides a single point of access for
 * input from and output to the network.
 */
public final class KadChannel implements Channel, il.technion.ewolf.kbr.MessageHandler {

  private static final Logger log = Logger.getLogger(KadChannel.class.getName());

  // Send/Receive data to/from this Kademlia endpoint
  private final KeybasedRouting kbrNode;

  // Transforms messages to serialized packets
  private final MessageOutputStream sendStream;
  // Receives messages for particular tags
  private final Map<String, MessageHandler> messageHandlers = new HashMap<>();

  @Inject
  private KadChannel(KeybasedRouting kbrNode) {
    this.kbrNode = kbrNode;
    this.sendStream = new KadSendMessageStream(kbrNode);
  }

  /**
   * Binds a {@link MessageHandler} to messages signed with a particular {@code tag}.
   */
  @Override
  public void registerMessageHandler(MessageHandler handler, String tag) {
    kbrNode.register(tag, this);
    messageHandlers.put(tag, handler);
  }

  @Override
  public void close() {
    messageHandlers.clear();
  }

  /**
   * Gets a {@link MessageOutputStream} capable of writing messages to the Kademlia network.
   */
  @Override
  public MessageOutputStream getMessageOutputStream() {
    return sendStream;
  }

  /**
   * Receives deserialized messages from the Kademlia node and passes them to the bound {@link
   * MessageHandler}.
   *
   * @param from    the Kademlia node that send the message.
   * @param tag     the tag with which the message is signed.
   * @param content the message.
   */
  @Override
  public void onIncomingMessage(Node from, String tag, Serializable content) {

    if (!(content instanceof Message)) {
      log.info("Invalid content from the Kademlia network");
      return;
    }

    // Look up the message handler associated with that tag and pass the message along.
    MessageHandler handler = messageHandlers.get(tag);
    if (handler != null) {
      handler.onIncomingMessage((Message) content);
    } else {
      log.info("Unhandled message from Kademlia network");
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
  @Override
  public Serializable onIncomingRequest(Node from, String tag, Serializable content) {

    if (!(content instanceof Message)) {
      log.info("Invalid content from the Kademlia network");
      return new ExceptionMessage(new InvalidContentException());
    }

    MessageHandler handler = messageHandlers.get(tag);
    if (handler != null) {
      return handler.onIncomingRequest((Message) content);
    } else {
      log.info("Unhandled message from Kademlia network");
      return new ExceptionMessage(new UnhandledRequestException());
    }
  }
}
