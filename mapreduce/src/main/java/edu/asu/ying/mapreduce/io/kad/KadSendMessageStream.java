package edu.asu.ying.mapreduce.io.kad;

import com.google.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import edu.asu.ying.mapreduce.io.MessageOutputStream;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.resource.ResourceIdentifier;
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
  private final ResourceIdentifier localUri;

  @Inject
  private KadSendMessageStream(final KeybasedRouting kadNode) throws URISyntaxException {
    this.kadNode = kadNode;
    this.localUri = this.createLocalUri();
  }

  /**
   * Returns the {@link ResourceIdentifier} associated with the local host. <p> The node identifier
   * format is as follows: <p> {@code node/node-key"}
   */
  private ResourceIdentifier createLocalUri() throws URISyntaxException {
    return new ResourceIdentifier("node", this.kadNode.getLocalNode().getKey().toBase64());
  }

  /**
   * Sends a message to the host identified on {@link Message#getDestinationUri()}. </p> The actual
   * number of messages sent is specified by {@link Message#getReplication()}.
   *
   * @param message the message to send, complete with the destination URI.
   * @return the number of message sent.
   */
  @Override
  public final int write(final Message message) throws IOException {
    message.setSourceUri(new ResourceIdentifier(message.getDestinationUri().getScheme(),
                                                this.localUri.getAddress()));

    // Identify the destination nodes and send the message to them
    // This line just decided what charset keys are in forever
    final String host = message.getDestinationUri().getHost();
    final Key destKey = new Key(message.getDestinationUri().getHost());

    final List<Node> foundNodes = this.kadNode.findNode(destKey);
    if (foundNodes.size() == 0) {
      // This should never happen
      throw new UnknownHostException();
    }

    final String scheme = message.getDestinationUri().getScheme();
    // Send the message to the k nearest nodes (defined on the message's replication property)
    final Iterator<Node> iter = foundNodes.iterator();
    int messageCount;
    for (messageCount = 0; iter.hasNext() && (messageCount < message.getReplication());
         messageCount++) {
      this.kadNode.sendMessage(iter.next(), scheme, message);
    }
    return messageCount;
  }
}
