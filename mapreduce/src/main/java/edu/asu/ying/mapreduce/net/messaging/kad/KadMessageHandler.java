package edu.asu.ying.mapreduce.net.messaging.kad;

import com.google.inject.Inject;

import java.io.IOException;
import java.io.Serializable;

import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.event.FilteredValueEventBase;
import edu.asu.ying.mapreduce.net.InvalidContentException;
import edu.asu.ying.mapreduce.net.messaging.AcknowledgementMessage;
import edu.asu.ying.mapreduce.net.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.net.messaging.IncomingMessageEvent;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@link KadMessageHandler} dispatches messages from the Kademlia network by firing a {@link
 * FilteredValueEvent}, allowing listeners to be notified of specific messages which they select by
 * attaching a filter to the event. </p> Each {@link KadMessageHandler} listens for messages on a
 * specific scheme.</br> E.g. if the message handler is bound to the scheme {@code resource}, it
 * will receive messages with URIs like: </p> {@code resource\host:port\type\name} </p> The event
 * can be injected anywhere using the annotation {@link IncomingMessageEvent}.
 */
public final class KadMessageHandler
    implements MessageHandler, il.technion.ewolf.kbr.MessageHandler {

  // Get our messages from here
  private final KeybasedRouting inputNode;

  // Event used to signal other listeners of incoming messages
  private final FilteredValueEvent<Message> incomingMessageEvent = new FilteredValueEventBase<>();

  @Inject
  private KadMessageHandler(final KeybasedRouting inputNode) {
    this.inputNode = inputNode;
  }

  /**************************************************
   * MessageHandler
   */
  /**
   * Binds to the Kademlia message dispatch, receiving messages with the tag {@code scheme}.
   */
  public final MessageHandler bind(final String scheme) {
    this.inputNode.register(scheme, this);
    return this;
  }

  /**
   * Provides a {@link FilteredValueEvent} that provides incoming messages to other listeners. </p>
   * Any class can receive messages of a specific type by attaching a filter and callback to this
   * event.
   */
  @Override
  public final FilteredValueEvent<Message> getIncomingMessageEvent() {
    return this.incomingMessageEvent;
  }

  /**************************************************
   * il.technion.ewolf.kbr.MessageHandler
   */

  /**
   * Relays an incoming message by firing a {@link FilteredValueEvent}.
   *
   * @param from    the {@link Node} sending the message
   * @param tag     the arrived message tag (always "mapreduce")
   * @param content the sent object
   */
  @Override
  public void onIncomingMessage(final Node from, final String tag, final Serializable content) {
    try {
      if (!(content instanceof Message)) {
        throw new InvalidContentException();
      }
      this.incomingMessageEvent.fire(this, (Message) content);
    } catch (final IOException e) {
      // TODO: logging
      e.printStackTrace();
    }
  }

  /**
   * Relays an incoming message via the {@link FilteredValueEvent} and returns an {@link
   * AcknowledgementMessage} to the sender. <p> If {@code content} is not a valid {@link Message},
   * returns an {@link InvalidContentException} to the sender.
   *
   * @param from    the {@link Node} sending the message
   * @param tag     the arrived message tag (always "mapreduce")
   * @param content the sent object
   * @return {@link AcknowledgementMessage} or {@link ExceptionMessage} signaling the reception
   *         success.
   */
  @Override
  public Serializable onIncomingRequest(final Node from, final String tag,
                                        final Serializable content) {
    if (!(content instanceof Message)) {
      return new ExceptionMessage(new InvalidContentException());
    }
    this.incomingMessageEvent.fire(this, (Message) content);
    return new AcknowledgementMessage();
  }
}