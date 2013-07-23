package edu.asu.ying.mapreduce.io.kad;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.net.NodeURI;
import edu.asu.ying.mapreduce.net.messaging.Message;
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

  public KadSendMessageStream(final KeybasedRouting kadNode) throws URISyntaxException {
    this.kadNode = kadNode;
    this.localUri = this.createLocalUri();
  }

  private NodeURI createLocalUri() {
    return new KadNodeURN(this.kadNode.getLocalNode().getKey().toBase64());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestinationNode()}.
   *
   * @param message the message to send, complete with the destination URI.
   */
  @Override
  public final void write(final Message message) throws IOException {
    message.setSourceNode(this.localUri);

    final Key destKey = message.getDestinationNode().toKey();

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    // Send the message to the k nearest nodes (defined on the message's replication property)
    final Iterator<Node> iter = foundNodes.iterator();
    int messageCount;
    for (messageCount = 0; iter.hasNext() && (messageCount < message.getReplication());
         messageCount++) {
      // TODO: Make use of kademlia tagged messages
      this.kadNode.sendMessage(iter.next(), "mapreduce", message);
    }
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
}
