package edu.asu.ying.p2p.io.kad;

import com.google.common.util.concurrent.ListenableFutureTask;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.p2p.io.MessageOutputStream;
import edu.asu.ying.p2p.node.kad.KadNodeIdentifier;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.io.message.Message;
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
  private final NodeIdentifier localUri;

  public KadSendMessageStream(final KeybasedRouting kadNode) {
    this.kadNode = kadNode;
    this.localUri = this.createLocalUri();
  }

  private NodeIdentifier createLocalUri() {
    return new KadNodeIdentifier(this.kadNode.getLocalNode().getKey());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestinationNode()}.
   *
   * @param message the message to send, complete with the destination URI.
   */
  @Override
  public final void write(final Message message) throws IOException {
    message.setSourceNode(this.localUri);

    final Key destKey = ((KadNodeIdentifier) message.getDestinationNode()).toKademliaKey();

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
    request.setSourceNode(this.localUri);

    final Key destKey = ((KadNodeIdentifier) request.getDestinationNode()).toKademliaKey();

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    return this.kadNode.sendRequest(foundNodes.get(0), request.getTag(), request);
  }

  @Override
  public Future<Serializable> writeAsyncRequest(Node node, Message request)
      throws IOException {

    request.setSourceNode(this.localUri);
    return this.kadNode.sendRequest(node, request.getTag(), request);
  }
}
