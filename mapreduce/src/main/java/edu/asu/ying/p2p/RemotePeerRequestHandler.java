package edu.asu.ying.p2p;

import com.google.inject.Inject;

import java.rmi.server.ExportException;
import java.util.logging.Logger;

import edu.asu.ying.common.remoting.Activator;
import edu.asu.ying.common.remoting.ClassNotExportedException;
import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.RequestMessage;
import edu.asu.ying.p2p.message.ResponseMessage;

/**
 * {@code RemotePeerRequestHandler} receives incoming requests for a remote peer reference and
 * responds with an instance of an exporter proxy wrapping the local {@link Activator}. </p> This
 * is
 * the primary transport mechanism for the P2P network overlaying the Kademlia network.
 */
public final class RemotePeerRequestHandler implements MessageHandler {

  private static final Logger log = Logger.getLogger(RemotePeerRequestHandler.class.getName());

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
      // Show-stopper
      log.severe("Unhandled exception exporting remote peer; node would be inaccessible if we"
                 + " continued.");
      throw new RuntimeException("Exception exporting remote peer; node would be inaccessible if we"
                                 + " continued.", e);
    }

    channel.registerMessageHandler(this, REMOTE_PEER_TAG);
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
