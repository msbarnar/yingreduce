package edu.asu.ying.p2p;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.rmi.server.ExportException;
import java.util.logging.Logger;

import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageHandler;
import edu.asu.ying.p2p.message.RequestMessage;
import edu.asu.ying.p2p.message.ResponseMessage;

/**
 * {@code RemotePeerRequestHandler} receives incoming requests for a remote peer reference and
 * responds with an instance of an exporter proxy wrapping the {@link LocalPeer}.
 * </p>
 * This is the primary transport mechanism for the P2P network overlaying the Kademlia network.
 */
public final class RemotePeerRequestHandler implements MessageHandler {

  private static final Logger log = Logger.getLogger(RemotePeerRequestHandler.class.getName());

  private static final String REMOTE_PEER_TAG = "p2p.remotepeer";

  public static RequestMessage createRequest() {
    return new RequestMessage(REMOTE_PEER_TAG);
  }

  private RemotePeer proxyInstance;
  private final Object proxyInstanceLock = new Object();

  private final RemotePeerExporter exporter;
  private final Provider<LocalPeer> localPeerProvider;

  @Inject
  private RemotePeerRequestHandler(Provider<LocalPeer> localPeerProvider,
                                   Channel channel,
                                   RemotePeerExporter exporter) {

    this.exporter = exporter;
    this.localPeerProvider = localPeerProvider;

    channel.registerMessageHandler(this, REMOTE_PEER_TAG);
  }

  /**
   * RequestHandler isn't meant to receive messages
   */
  @Override
  public void onIncomingMessage(Message message) {
  }

  @Override
  public Message onIncomingRequest(Message request) {
    final ResponseMessage response = ResponseMessage.inResponseTo(request);
    // Lazily export the local peer
    if (proxyInstance == null) {
      synchronized (proxyInstanceLock) {
        if (proxyInstance == null) {
          try {
            proxyInstance = exporter.export(localPeerProvider.get());
          } catch (ExportException e) {
            // Show-stopper
            throw new RuntimeException("Exception exporting remote peer; node would be inaccessible"
                                       + " if we continued.", e);
          }
        }
      }
    }
    response.setContent(proxyInstance);
    return response;
  }
}
