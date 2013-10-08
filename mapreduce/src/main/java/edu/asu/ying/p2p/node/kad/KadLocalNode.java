package edu.asu.ying.p2p.node.kad;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import edu.asu.ying.mapreduce.common.RemoteSink;
import edu.asu.ying.mapreduce.database.page.Page;
import edu.asu.ying.mapreduce.database.page.RemotePageSink;
import edu.asu.ying.mapreduce.database.page.RemotePageSinkImpl;
import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.RemoteScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.p2p.LocalNode;
import edu.asu.ying.p2p.NodeIdentifier;
import edu.asu.ying.p2p.RemoteNode;
import edu.asu.ying.p2p.io.Channel;
import edu.asu.ying.p2p.io.InvalidContentException;
import edu.asu.ying.p2p.io.message.RequestMessage;
import edu.asu.ying.p2p.io.message.ResponseMessage;
import edu.asu.ying.p2p.node.NodeNotFoundException;
import edu.asu.ying.p2p.rmi.RMIActivator;
import edu.asu.ying.p2p.rmi.RMIActivatorImpl;
import edu.asu.ying.p2p.rmi.RMIRequestHandler;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 *
 */
public final class KadLocalNode implements LocalNode {

  /**
   * Provides the implementation of {@code RemoteNode} which will be accessible by remote peers when
   * exported. The proxy implementation glues the remote node interface to the concrete local node
   * implementation while implementing the appropriate patterns to be RMI-compatible.
   */
  private final class KadRemoteNodeImpl implements RemoteNode {

    private final LocalNode localNode;

    private KadRemoteNodeImpl(final LocalNode localNode) {
      this.localNode = localNode;
    }

    public final NodeIdentifier getIdentifier() throws RemoteException {
      return this.localNode.getIdentifier();
    }

    public final RemoteScheduler getScheduler() throws RemoteException {
      return this.localNode.getScheduler().getProxy();
    }

    public final long getTimeMs() throws RemoteException {
      return System.currentTimeMillis();
    }

    @Override
    public RemoteSink<Page> getPageSink() throws RemoteException {
      return this.localNode.getPageSink();
    }
  }

  /**
   * *******************************************************************************************
   */

  // Local kademlia node
  private final KeybasedRouting kbrNode;
  private final NodeIdentifier localUri;

  // Pipe to the kad network
  private final Channel networkChannel;

  // Getting RMI references to neighbors is expensive, so cache the reference every time we get one
  private Map<Node, RemoteNode> neighborsCache = new HashMap<>();

  // Manages RMI export of objects for access by remote peers.
  private final RMIActivator activator;

  // Glue between the remote node interface and the local node implementation.
  private final RemoteNode nodeProxy;
  // Necessary to keep alive the glue implementation
  private final RemoteNode nodeProxyTarget;

  // Schedules mapreduce jobs and tasks
  private final LocalScheduler scheduler;

  // Accepts pages from the network
  private final RemotePageSink pageSink;
  private final RemotePageSink pageSinkProxy;


  public KadLocalNode(final int port) throws InstantiationException {

    // The local Kademlia node for node discovery
    this.kbrNode = KademliaNetwork.createNode(port);  // throws InstantiationException
    this.localUri = new KadNodeIdentifier(this.kbrNode.getLocalNode().getKey());

    this.networkChannel = KademliaNetwork.createChannel(this.kbrNode);

    // Start the remote reference provider
    this.activator = new RMIActivatorImpl();
    // Start the scheduler
    this.scheduler = new SchedulerImpl(this);
    this.scheduler.export(this.activator);
    this.scheduler.start();
    // Start the interface for sinking remote pages
    this.pageSink = new RemotePageSinkImpl();
    this.pageSinkProxy = this.activator.bind(RemotePageSink.class).toInstance(this.pageSink);

    // Allow peers to access the node and scheduler remotely.
    this.nodeProxyTarget = new KadRemoteNodeImpl(this);
    this.nodeProxy = this.activator.bind(RemoteNode.class).toInstance(this.nodeProxyTarget);

    // Allow peers to discover this node's RMI interfaces.
    RMIRequestHandler.exportNodeToChannel(this, networkChannel);

    /*System.out.println(String.format("Local node %s is listening on port %d",
                                     this.localUri.toString(),
                                     port));
                                     */
  }


  public final void join(final URI bootstrap) throws IOException {
    try {
      final List<URI> bootstrapUris = Arrays.asList(URI.create(bootstrap.toString()));
      this.kbrNode.join(bootstrapUris);

    } catch (final IllegalStateException e) {
      throw new NodeNotFoundException(bootstrap);
    }
  }


  // Don't let one thread munge up the cache while another reads it
  // TODO: 50/50 chance I'll need to be able to dirty this cache if the proxy fails
  public synchronized Collection<RemoteNode> getNeighbors() {
    final List<Node> kadNodes = this.kbrNode.getNeighbours();

    // To prune old nodes in one pass, update the whole cache every time keeping old values.
    final Map<Node, RemoteNode> newCache = new HashMap<>();
    for (final Node kadNode : kadNodes) {
      RemoteNode rmiRef = this.neighborsCache.get(kadNode);
      // Update missing refs
      if (rmiRef == null) {
        rmiRef = this.importProxyTo(kadNode);
        if (rmiRef == null) {
          throw new NullPointerException("Couldn't get RMI reference to remote node.");
        }
      }
      // Retain only nodes that are still connected
      newCache.put(kadNode, rmiRef);
    }

    // Keep the cache up to date
    this.neighborsCache = newCache;

    return Collections.unmodifiableCollection(this.neighborsCache.values());
  }


  public RemoteNode findNode(final NodeIdentifier uri) throws UnknownHostException {
    final Key key = this.kbrNode.getKeyFactory().create(uri.toString());
    final List<il.technion.ewolf.kbr.Node> kadNodes = this.kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      throw new UnknownHostException(uri.getKey());
    }
    return this.importProxyTo(kadNodes.get(0));
  }


  public LocalScheduler getScheduler() {
    return this.scheduler;
  }

  @Override
  public RemoteSink<Page> getPageSink() {
    return this.pageSinkProxy;
  }


  public RMIActivator getActivator() {
    return this.activator;
  }


  public NodeIdentifier getIdentifier() {
    return this.localUri;
  }

  public RemoteNode getProxy() {
    return this.nodeProxy;
  }

  private RemoteNode importProxyTo(final il.technion.ewolf.kbr.Node node) {
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
      final ResponseMessage rm = ((ResponseMessage) resp);
      if (rm.getException() != null) {
        throw rm.getException();
      }
      return (RemoteNode) ((ResponseMessage) resp).getContent();

    } catch (final Exception e) {

      // TODO: Logging
      e.printStackTrace();
      return null;
    }
  }
}
