package edu.asu.ying.mapreduce.node.io.kad;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
@Singleton
public final class KadChannel implements Channel, il.technion.ewolf.kbr.MessageHandler {

  private final KeybasedRouting kbrNode;

  private final MessageOutputStream sendStream;
  private final Map<String, MessageHandler> messageHandlers = new HashMap<>();

  @Inject
  private KadChannel(final KeybasedRouting kbrNode,
                     final @SendMessageStream MessageOutputStream sendStream) {

    this.kbrNode = kbrNode;
    this.sendStream = sendStream;
  }

  @Override
  public void registerMessageHandler(final MessageHandler handler, final String tag) {
    this.kbrNode.register(tag, this);
    this.messageHandlers.put(tag, handler);
  }

  @Override
  public void sendMessage(final Message message) throws IOException {
    this.sendStream.write(message);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <TRequest extends Message, TResponse extends Message> ListenableFuture<TResponse>
  sendRequestAsync(final TRequest request, final Class<TResponse> responseType) throws IOException {

    return this.sendStream
  }

  @Override
  public <T extends Message, V extends Message> T sendRequest(final V request,
                                                              final Class<T> responseType)
      throws IOException, ExecutionException, InterruptedException {

    return this.sendRequestAsync(request, responseType).get();
  }

  @Override
  public void onIncomingMessage(Node from, String tag, Serializable content) {
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
  public Serializable onIncomingRequest(Node from, String tag, Serializable content) {
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
