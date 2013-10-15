package edu.asu.ying.p2p;

import com.google.inject.Inject;

import java.rmi.server.ExportException;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.ClassNotExportedException;
import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.RequestMessage;
import edu.asu.ying.p2p.message.ResponseMessage;
import edu.asu.ying.p2p.rmi.RemotePeer;
import edu.asu.ying.p2p.rmi.RemotePeerExporter;

/**
 * {@code RemotePeerRequestHandler} receives incoming requests for a remote peer reference and
 * responds with an instance of an exporter proxy wrapping the local {@link Activator}. </p> This is
 * the primary transport mechanism for the P2P network overlaying the Kademlia network.
 */
public final class RemotePeerRequestHandler implements MessageHandler {

  private static final String REMOTE_PEER_TAG = "p2p.remotepeer";

  public static RequestMessage createRequest() {
    return new RequestMessage(REMOTE_PEER_TAG);
  }

  private final RemotePeer proxyInstance;

  @Inject
  private RemotePeerRequestHandler(Activator activator,
                                   Channel channel,
                                   RemotePeerExporter exporter) {
    try {
      this.proxyInstance = exporter.export(activator);
    } catch (ExportException e) {
      // TODO: Logging
      throw new RuntimeException("Exception exporting remote peer", e);
    }

    channel.registerMessageHandler(this, "p2p.remotepeer");
  }

  @Override
  public void onIncomingMessage(Message message) {
  }

  @Override
  public Message onIncomingRequest(Message request) {
    final ResponseMessage response = ResponseMessage.inResponseTo(request);
    if (proxyInstance == null) {
      response.setException(new ClassNotExportedException());
      return response;
    }
    response.setContent(proxyInstance);
    return response;
  }
}
