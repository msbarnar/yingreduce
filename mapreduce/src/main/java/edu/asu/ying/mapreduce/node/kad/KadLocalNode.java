package edu.asu.ying.mapreduce.node.kad;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.asu.ying.mapreduce.node.io.Channel;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.mapreduce.node.*;
import edu.asu.ying.mapreduce.node.io.InvalidContentException;
import edu.asu.ying.mapreduce.node.io.message.Message;
import edu.asu.ying.mapreduce.node.io.message.RequestMessage;
import edu.asu.ying.mapreduce.node.io.message.ResponseMessage;
import edu.asu.ying.mapreduce.node.rmi.Activator;
import edu.asu.ying.mapreduce.node.rmi.ActivatorImpl;
import edu.asu.ying.mapreduce.mapreduce.scheduling.Scheduler;
import edu.asu.ying.mapreduce.node.rmi.NodeProxyRequestHandler;
import edu.asu.ying.mapreduce.node.rmi.RemoteNodeProxy;
import il.technion.ewolf.kbr.*;
import il.technion.ewolf.kbr.Node;


/**
 *
 */
public final class KadLocalNode
    implements LocalNode {

  // Local kademlia node
  private final KeybasedRouting kbrNode;
  private final NodeURI localUri;

  // Pipe to the kad network
  private final Channel networkChannel;

  // Provides RMI references to the scheduler
  private final Activator activator;

  // Schedules mapreduce jobs and tasks
  private final Scheduler scheduler;

  public KadLocalNode(final int port) throws InstantiationException {

    // The local Kademlia node for node discovery
    this.kbrNode = KademliaNetwork.createNode(port);
    this.localUri = new KadNodeURI(this.kbrNode.getLocalNode().getKey());

    this.networkChannel = KademliaNetwork.createChannel(this.kbrNode);

    // Start the remote to provide Scheduler references
    this.activator = new ActivatorImpl();
    // Start the scheduler with a reference to the local node for finding neighbors
    this.scheduler = new SchedulerImpl(this);

    // Expose this local node to ServerNodeProxy requests via the request handler
    NodeProxyRequestHandler.exposeNodeToChannel(this, networkChannel);

    System.out.println("The local Kademlia node is listening.");
  }

  @Override
  public final void join(final NodeURL bootstrap) throws IOException {
    try {
      final List<URI> bootstrapUris = Arrays.asList(bootstrap.toURI());
      this.kbrNode.join(bootstrapUris);

    } catch (final IllegalStateException e) {
      throw new NodeNotFoundException(bootstrap);
    }

    // TODO: Logging
    System.out.println(String.format("Node %s connected to bootstrap node %s", this.localUri,
                                     bootstrap.toString()));
  }

  @Override
  public List<RemoteNodeProxy> getNeighbors() {
    final List<il.technion.ewolf.kbr.Node> kadNodes = this.kbrNode.getNeighbours();
    final List<RemoteNodeProxy> nodeProxies = new ArrayList<>();

    for (final il.technion.ewolf.kbr.Node kadNode : kadNodes) {
      final RemoteNodeProxy proxy = this.importProxyTo(kadNode);
      if (proxy != null) {
        nodeProxies.add(proxy);
      }
    }

    return nodeProxies;
  }

  /**
   * The local node returns a concrete reference to the scheduler.
   */
  @Override
  public Scheduler getScheduler() {
    return this.scheduler;
  }

  @Override
  public Activator getActivator() {
    return this.activator;
  }

  @Override
  public NodeURI getNodeURI() {
    return this.localUri;
  }

  private RemoteNodeProxy importProxyTo(final il.technion.ewolf.kbr.Node node) {
    final RequestMessage request = new RequestMessage("node.remote-proxy");
    final Future<Serializable> response;
    try {
      response =
        this.networkChannel.getMessageOutputStream().writeAsyncRequest(node, request);

    } catch (final IOException e) {
      // TODO: Logging
      e.printStackTrace();
      return null;
    }

    try {
      final Serializable resp = response.get();
      if (!(resp instanceof ResponseMessage)) {
        throw new InvalidContentException();
      }
      return (RemoteNodeProxy) ((ResponseMessage) resp).getContent();

    } catch (final ExecutionException | InterruptedException
        | InvalidContentException | ClassCastException e) {

      // TODO: Logging
      e.printStackTrace();
      return null;
    }
  }
}
