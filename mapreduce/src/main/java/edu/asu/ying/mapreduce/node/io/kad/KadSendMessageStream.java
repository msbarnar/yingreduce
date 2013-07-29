package edu.asu.ying.mapreduce.node.io.kad;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.asu.ying.mapreduce.node.io.MessageOutputStream;
import edu.asu.ying.mapreduce.node.NodeURI;
import edu.asu.ying.mapreduce.node.kad.KadNodeURI;
import edu.asu.ying.mapreduce.node.io.message.Message;
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
  private final NodeURI localUri;

  public KadSendMessageStream(final KeybasedRouting kadNode) {
    this.kadNode = kadNode;
    this.localUri = this.createLocalUri();
  }

  private NodeURI createLocalUri() {
    return new KadNodeURI(this.kadNode.getLocalNode().getKey());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestinationNode()}.
   *
   * @param message the message to send, complete with the destination URI.
   */
  @Override
  public final void write(final Message message) throws IOException {
    message.setSourceNode(this.localUri);

    final Key destKey = ((KadNodeURI) message.getDestinationNode()).toKademliaKey();

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

    final Key destKey = ((KadNodeURI) request.getDestinationNode()).toKademliaKey();

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    return this.kadNode.sendRequest(foundNodes.get(0), request.getTag(), request);
  }
}
