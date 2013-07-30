package edu.asu.ying.p2p.rmi;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;

/**
 * {@code ActivatorRequestHandler} listens for requests from the network and returns a
 * {@link ResponseMessage} wrapping a {@link java.rmi.Remote} proxy to the server-side
 * {@link RemoteActivator}.
 * </p>
 * The lifetime and instantiation of the activator proxy is controlled by the
 * {@link ServerActivator} instance providing it; the request handler is merely an intermediate.
 */
public final class ActivatorRequestHandler implements MessageHandler {

  /**
   * Creates an {@link ActivatorRequestHandler} which provides {@link RemoteActivator} referencing
   * the given {@link LocalNode}'s {@link ServerActivator}.
   * @param node the node to be made accessible to peers on the network.
   * @param networkChannel the channel through which the node will be accessible.
   * @return the new request handler.
   */
  public static ActivatorRequestHandler exportNodeToChannel(final LocalNode node,
                                                            final Channel networkChannel) {
    return new ActivatorRequestHandler(node, networkChannel);
  }

  // The proxy instance to include in responses
  private final RemoteActivator instance;

  // TODO: change how we handle tags?
  private final String tag = "node.remote-proxy";

  /**
   * Gets an instance of the {@link RemoteActivator} proxy and registers the message handler on the
   * channel.
   */
  private ActivatorRequestHandler(final LocalNode localNode, final Channel networkChannel) {
    this.instance = localNode.getActivator().export();
    networkChannel.registerMessageHandler(this, this.tag);
  }

  /**
   * The request handler is meant only to respond to activator requests.
   */
  @Override
  public void onIncomingMessage(final Message message) {
  }

  /**
   * Responds to {@code request} with a {@link ResponseMessage} wrapping the {@link RemoteActivator}
   * instance obtained from the {@link LocalNode} passed in
   * {@link ActivatorRequestHandler#ActivatorRequestHandler(edu.asu.ying.p2p.LocalNode,
   edu.asu.ying.mapreduce.node.io.Channel)}.
   */
  @Override
  public Message onIncomingRequest(final Message request) {
    final ResponseMessage response = ResponseMessage.inResponseTo(request);
    response.setContent(this.instance);
    return response;
  }

  @Override
  public final String getTag() {
    return this.tag;
  }
}
