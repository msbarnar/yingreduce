package edu.asu.ying.p2p.rmi;

import java.rmi.server.ExportException;

import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.net.Channel;
import edu.asu.ying.p2p.net.message.Message;
import edu.asu.ying.p2p.net.message.MessageHandler;
import edu.asu.ying.p2p.net.message.ResponseMessage;

/**
 * {@code RMIRequestHandler} listens for requests from the network and returns a {@link
 * ResponseMessage} wrapping a {@link java.rmi.Remote} proxy to the server-side node.
 */
public final class RMIRequestHandler implements MessageHandler {

  /**
   * Creates an {@link RMIRequestHandler} which provides instances of the {@link
   * edu.asu.ying.p2p.RemotePeer} proxy to the {@link edu.asu.ying.p2p.LocalPeer} for access by
   * remote peers.
   *
   * @param peer           the node to be made accessible to peers on the network.
   * @param networkChannel the channel through which the node will be accessible.
   * @return the new request handler.
   */
  public static RMIRequestHandler exportNodeToChannel(final LocalPeer peer,
                                                      final Channel networkChannel)
      throws ExportException {

    return new RMIRequestHandler(peer, networkChannel);
  }

  // The proxy instance to include in responses
  private final RemotePeer instance;

  // TODO: change how we handle tags?
  private final String tag = "node.remote-proxy";

  /**
   * Gets an instance of the {@link edu.asu.ying.p2p.RemotePeer} proxy and registers the message
   * handler on the channel. </p> The {@link edu.asu.ying.p2p.RemotePeer} should have already been
   * bound on the local node's {@link Activator} via {@link Activator#bind}.
   */
  private RMIRequestHandler(final LocalPeer localPeer, final Channel networkChannel)
      throws ExportException {

    // Cache the RMI proxy instance for the peer so we can respond to requests with it
    this.instance = localPeer.getActivator().bind(RemotePeer.class)
        .to(localPeer)
        .wrappedBy(RemotePeerWrapper.class);

    if (this.instance == null) {
      throw new ExportException("Failed to export activator; this node will be unable to "
                                + "participate in the network.");
    }
    networkChannel.registerMessageHandler(this, this.tag);
  }

  /**
   * The request handler is meant only to respond to activator requests.
   */
  @Override
  public void onIncomingMessage(final Message message) {
    // TODO: Logging
    System.out
        .println("Unexpected message to activator request handler: ".concat(message.toString()));
  }

  /**
   * Responds to {@code request} with a {@link ResponseMessage} wrapping the {@link
   * edu.asu.ying.p2p.RemotePeer} proxy for the local node.
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
