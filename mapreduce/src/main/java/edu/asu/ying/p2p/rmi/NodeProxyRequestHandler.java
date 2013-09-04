package edu.asu.ying.p2p.rmi;

import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;

/**
 * {@code NodeProxyRequestHandler} listens for requests from the network and returns a
 * {@link ResponseMessage} wrapping a {@link java.rmi.Remote} proxy to the server-side
 * node.
 */
public final class NodeProxyRequestHandler implements MessageHandler {

  /**
   * Creates an {@link NodeProxyRequestHandler} which provides {@link RemoteActivator} referencing
   * the given {@link LocalNode}'s {@link ServerActivator}.
   * @param node the node to be made accessible to peers on the network.
   * @param networkChannel the channel through which the node will be accessible.
   * @return the new request handler.
   */
  public static NodeProxyRequestHandler exportNodeToChannel(final LocalNode node,
                                                            final Channel networkChannel) {
    return new NodeProxyRequestHandler(node, networkChannel);
  }

  // The proxy instance to include in responses
  private final RemoteActivator instance;

  // TODO: change how we handle tags?
  private final String tag = "node.remote-proxy";

  /**
   * Gets an instance of the {@link RemoteActivator} proxy and registers the message handler on the
   * channel.
   */
  private NodeProxyRequestHandler(final LocalNode localNode, final Channel networkChannel) {
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
   * {@link NodeProxyRequestHandler#NodeProxyRequestHandler(edu.asu.ying.p2p.LocalNode,
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
