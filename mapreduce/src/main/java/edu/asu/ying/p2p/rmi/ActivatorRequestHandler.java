package edu.asu.ying.p2p.rmi;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;

public final class ActivatorRequestHandler implements MessageHandler {

  public static ActivatorRequestHandler exposeNodeToChannel(final LocalNode node,
                                                            final Channel networkChannel) {
    return new ActivatorRequestHandler(node, networkChannel);
  }

  private final RemoteActivator instance;

  private final String tag = "node.remote-proxy";

  private ActivatorRequestHandler(final LocalNode localNode, final Channel networkChannel) {
    instance = localNode.getActivator().export();
    networkChannel.registerMessageHandler(this, this.tag);
  }

  @Override
  public void onIncomingMessage(final Message message) {
    // TODO: Logging
    throw new NotImplementedException();
  }

  @Override
  public Message onIncomingRequest(final Message request) {
    final ResponseMessage response = ResponseMessage.inResponseTo(request);

    // Bind the proxy to the local scheduler
    response.setContent(this.instance);

    return response;
  }

  @Override
  public final String getTag() {
    return this.tag;
  }
}
