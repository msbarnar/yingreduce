package edu.asu.ying.mapreduce.rmi.node;

import java.io.IOException;
import java.rmi.RemoteException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.node.LocalNode;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.messaging.Message;

/**
 * {@code NodeProxyRequestHandler} receives {@link NodeProxyRequest} messages and responds with an
 * appropriate {@link NodeProxy} referencing the local node.
 */
public final class NodeProxyRequestHandler implements EventHandler<Message> {

  /**
   * Creates a {@code NodeProxyRequestHandler} listening on the specified channel for
   * {@link NodeProxyRequest} messages and responding with proxy instances to the specified local
   * node instance.
   * @return the request handler exposing the local node.
   */
  public static NodeProxyRequestHandler exposeNodeToChannel(final LocalNode node,
                                                            final Channel networkChannel) {
    return new NodeProxyRequestHandler(node, networkChannel);
  }

  private final LocalNode localNode;
  private final Channel networkChannel;

  private NodeProxyRequestHandler(final LocalNode localNode, final Channel networkChannel) {
    this.localNode = localNode;
    this.networkChannel = networkChannel;

    // Register to receive NodeProxyRequest messages
    this.networkChannel.getIncomingMessageHandler().getIncomingMessageEvent().attach(
        FilterClass.is(NodeProxyRequest.class), this);
  }

  /**
   * Receives a {@link NodeProxyRequest} message from the incoming message handler.
   *
   * @return true, so that it always remains bound to the message event.
   */
  @Override
  public final boolean onEvent(final @Nonnull Object sender, final @Nullable Message request) {
    if (request == null) {
      return true;
    }
    Message response = this.processRequest((NodeProxyRequest) request);
    try {
      this.networkChannel.sendMessage(response);
    } catch (final IOException e) {
      // TODO: logging
      e.printStackTrace();
    }

    // Always return true to stay bound to the event
    return true;
  }

  private NodeProxyResponse processRequest(final NodeProxyRequest request) {
    final NodeProxyResponse response = NodeProxyResponse.inResponseTo(request);

    // Bind the proxy to the local scheduler
    try {
      final NodeProxy proxyInstance = NodeProxyImpl.createProxyTo(this.localNode);
      response.setInstance(proxyInstance);

    } catch (final RemoteException e) {
      response.setException(e);
    }

    return response;
  }
}
