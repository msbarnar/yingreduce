package edu.asu.ying.mapreduce.node.io.kad;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.asu.ying.mapreduce.common.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.common.filter.FilterString;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.InvalidContentException;
import edu.asu.ying.mapreduce.node.io.MessageOutputStream;
import edu.asu.ying.mapreduce.node.io.SendMessageStream;
import edu.asu.ying.mapreduce.node.io.UnhandledRequestException;
import edu.asu.ying.mapreduce.node.io.message.ExceptionMessage;
import edu.asu.ying.mapreduce.node.io.message.FilterMessage;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;

/**
 * {@code KadChannel} encompasses the {@link MessageHandler} and {@link MessageOutputStream} tied
 * to the underlying Kademlia network. The {@code KadChannel} provides a single point of access for
 * input from and output to the network.
 */
public final class KadChannel implements Channel, il.technion.ewolf.kbr.MessageHandler {

  private final KeybasedRouting kbrNode;

  private final MessageOutputStream sendStream;
  private final Map<String, MessageHandler> messageHandlers = new HashMap<>();

  public KadChannel(final KeybasedRouting kbrNode) {
    this.kbrNode = kbrNode;
    this.sendStream = new KadSendMessageStream(kbrNode);
  }

  @Override
  public final void registerMessageHandler(final MessageHandler handler, final String tag) {
    this.kbrNode.register(tag, this);
    this.messageHandlers.put(tag, handler);
    // TODO: Logging
    System.out.println("Bound request handler for '".concat(tag).concat("'"));
  }

  @Override
  public final MessageOutputStream getMessageOutputStream() {
    return this.sendStream;
  }

  @Override
  public final void onIncomingMessage(final Node from, final String tag,
                                      final Serializable content) {

    if (!(content instanceof Message)) {
      // TODO: Logging
      return;
    }

    final MessageHandler handler = this.messageHandlers.get(tag);
    if (handler != null) {
      handler.onIncomingMessage((Message) content);
    }
  }

  @Override
  public final Serializable onIncomingRequest(final Node from, final String tag,
                                              final Serializable content) {

    if (!(content instanceof Message)) {
      // TODO: Logging
      return new ExceptionMessage(new InvalidContentException());
    }

    final MessageHandler handler = this.messageHandlers.get(tag);
    if (handler != null) {
      return handler.onIncomingRequest((Message) content);
    } else {
      return new ExceptionMessage(new UnhandledRequestException());
    }
  }
}
