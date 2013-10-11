package edu.asu.ying.p2p.net.kad;

import com.google.common.util.concurrent.ListenableFutureTask;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.kad.KadPeerIdentifier;
import edu.asu.ying.p2p.net.message.Message;
import edu.asu.ying.p2p.net.message.MessageOutputStream;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@link KadSendMessageStream} is a {@link MessageOutputStream} that serializes messages to remote
 * nodes on the Kademlia network.
 */
public final class KadSendMessageStream
    implements MessageOutputStream {

  private final KeybasedRouting kadNode;
  // Sign outgoing messages with the sender peer identifier
  private final PeerIdentifier localIdentifier;

  public KadSendMessageStream(final KeybasedRouting kadNode) {
    this.kadNode = kadNode;
    // Sign outgoing messages with the local peer key
    this.localIdentifier = new KadPeerIdentifier(this.kadNode.getLocalNode().getKey());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestination()}.
   *
   * @param message the message to send, complete with the destination URI.
   */
  @Override
  public final void write(final Message message) throws IOException {
    message.setSender(this.localIdentifier);

    final Key destKey = ((KadPeerIdentifier) message.getDestination()).toKademliaKey();

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    this.kadNode.sendMessage(foundNodes.get(0), message.getTag(), message);
  }

  @Override
  public final ListenableFutureTask<Boolean> writeAsync(final Message message) throws IOException {

    // Write the message in a background thread
    final ListenableFutureTask<Boolean> asyncTask = ListenableFutureTask.create(
        new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            KadSendMessageStream.this.write(message);
            return true;
          }
        });

    // Run the task in a background process
    ExecutorService executor = Executors.newSingleThreadExecutor();
    executor.execute(asyncTask);

    return asyncTask;
  }

  @Override
  public final Future<Serializable> writeAsyncRequest(final Message request) throws IOException {
    request.setSender(this.localIdentifier);

    final Key destKey = ((KadPeerIdentifier) request.getDestination()).toKademliaKey();

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    return this.kadNode.sendRequest(foundNodes.get(0), request.getTag(), request);
  }

  @Override
  public Future<Serializable> writeAsyncRequest(final Node node, final Message request)
      throws IOException {

    request.setSender(this.localIdentifier);
    return this.kadNode.sendRequest(node, request.getTag(), request);
  }
}