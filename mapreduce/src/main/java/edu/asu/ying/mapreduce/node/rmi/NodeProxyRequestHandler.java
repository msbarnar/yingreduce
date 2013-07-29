package edu.asu.ying.mapreduce.node.rmi;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.rmi.RemoteException;

import edu.asu.ying.mapreduce.node.LocalNode;
import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.node.io.MessageHandler;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;

public final class NodeProxyRequestHandler implements MessageHandler {

  public static NodeProxyRequestHandler exposeNodeToChannel(final LocalNode node,
                                                            final Channel networkChannel) {
    return new NodeProxyRequestHandler(node, networkChannel);
  }

  private final LocalNode localNode;

  private final String tag = "node.remote-proxy";

  private NodeProxyRequestHandler(final LocalNode localNode, final Channel networkChannel) {
    this.localNode = localNode;

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

    try {
      // Bind the proxy to the local scheduler
      final RemoteNodeProxy proxyInstance = ServerNodeProxy.createProxyTo(this.localNode);
      response.setContent(proxyInstance);

    } catch (final RemoteException e) {
      response.setException(e);
    }

    return response;
  }

  @Override
  public final String getTag() {
    return this.tag;
  }
}
