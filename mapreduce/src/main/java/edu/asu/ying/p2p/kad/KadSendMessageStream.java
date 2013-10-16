package edu.asu.ying.p2p.kad;

import com.google.common.util.concurrent.ListenableFutureTask;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.p2p.PeerName;
import edu.asu.ying.p2p.message.Message;
import edu.asu.ying.p2p.message.MessageOutputStream;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@link KadSendMessageStream} is a {@link MessageOutputStream} that serializes messages to remote
 * nodes on the Kademlia network.
 */
final class KadSendMessageStream implements MessageOutputStream {

  private final KeybasedRouting kadNode;
  // Sign outgoing messages with the sender peer name
  private final PeerName peerName;

  KadSendMessageStream(KeybasedRouting kadNode) {
    this.kadNode = kadNode;
    // Sign outgoing messages with the local peer key
    this.peerName = new KadPeerName(kadNode.getLocalNode().getKey());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestination()}.
   *
   * @param message the message to send, complete with the destination URI.
   */
  @Override
  public void write(Message message) throws IOException {
    message.setSender(peerName);

    Key destKey = ((KadPeerName) message.getDestination()).toKademliaKey();

    List<Node> foundNodes = kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    kadNode.sendMessage(foundNodes.get(0), message.getTag(), message);
  }

  @Override
  public ListenableFutureTask<Boolean> writeAsync(final Message message) throws IOException {

    // Write the message in a background thread
    ListenableFutureTask<Boolean> asyncTask = ListenableFutureTask.create(
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
  public Future<Serializable> writeAsyncRequest(Message request) throws IOException {
    request.setSender(this.peerName);

    Key destKey = ((KadPeerName) request.getDestination()).toKademliaKey();

    List<Node> foundNodes = kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    return kadNode.sendRequest(foundNodes.get(0), request.getTag(), request);
  }

  @Override
  public Future<Serializable> writeAsyncRequest(Node node, Message request) throws IOException {

    request.setSender(peerName);
    return kadNode.sendRequest(node, request.getTag(), request);
  }
}
