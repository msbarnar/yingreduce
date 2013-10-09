package edu.asu.ying.p2p.kad;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
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
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.net.Channel;
import edu.asu.ying.p2p.net.InvalidContentException;
import edu.asu.ying.p2p.net.RequestMessage;
import edu.asu.ying.p2p.net.ResponseMessage;
import edu.asu.ying.p2p.rmi.RMIActivator;
import edu.asu.ying.p2p.rmi.RMIActivatorImpl;
import edu.asu.ying.p2p.rmi.RMIRequestHandler;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@code KadLocalPeer} implements the local high-level peer-to-peer behavior on top of the Kademlia
 * routing system. </p> The local peer maintains the Kademlia node, the local job scheduler, and the
 * local database interface.
 */
public final class KadLocalPeer implements LocalPeer {

  /**
   * Provides the implementation of {@code RemotePeer} which will be accessible by remote peers when
   * exported. The proxy implementation glues the remote node interface to the concrete local node
   * implementation while implementing the appropriate patterns to be RMI-compatible.
   */
  private final class KadRemotePeerImpl implements RemotePeer {

    private final LocalPeer localPeer;

    private KadRemotePeerImpl(final LocalPeer localPeer) {
      this.localPeer = localPeer;
    }

    @Override
    public final PeerIdentifier getIdentifier() throws RemoteException {
      return this.localPeer.getIdentifier();
    }

    @Override
    public final RemoteScheduler getScheduler() throws RemoteException {
      return this.localPeer.getScheduler().getProxy();
    }

    @Override
    public RemoteSink<Page> getPageSink() throws RemoteException {
      return this.localPeer.getPageSink();
    }

    @Override
    public final long getCurrentTimeMilliseconds() throws RemoteException {
      return System.currentTimeMillis();
    }
  }

  /**
   * *******************************************************************************************
   */

  // Local kademlia node
  private final KeybasedRouting kbrNode;
  private final PeerIdentifier localIdentifier;

  // Pipe to the kad network
  private final Channel networkChannel;

  // Getting RMI references to neighbors is expensive, so cache the reference every time we get one
  private Map<Node, RemotePeer> neighborsCache = new HashMap<>();

  // Manages RMI export of objects for access by remote peers.
  private final RMIActivator activator;

  // Glue between the remote node interface and the local node implementation.
  private final RemotePeer nodeProxy;
  // Necessary to keep alive the glue implementation
  private final RemotePeer nodeProxyTarget;

  // Schedules mapreduce jobs and tasks
  private final LocalScheduler scheduler;

  // Accepts pages from the network
  private final RemotePageSink pageSink;


  public KadLocalPeer(final int port) throws InstantiationException {

    // The local Kademlia node for node discovery
    this.kbrNode = KademliaNetwork.createNode(port);  // throws InstantiationException
    this.localIdentifier =
        new KadPeerIdentifier(this.kbrNode.getLocalNode().getInetAddress().toString());

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
    this.nodeProxyTarget = new KadRemotePeerImpl(this);
    this.nodeProxy = this.activator.bind(RemotePeer.class).toInstance(this.nodeProxyTarget);

    // Allow peers to discover this node's RMI interfaces.
    RMIRequestHandler.exportNodeToChannel(this, networkChannel);
  }


  public final void join(final URI bootstrap) throws IOException {
    try {
      final List<URI> bootstrapUris = Arrays.asList(URI.create(bootstrap.toString()));
      this.kbrNode.join(bootstrapUris);

    } catch (final IllegalStateException e) {
      throw new PeerNotFoundException(bootstrap);
    }
  }


  // Don't let one thread munge up the cache while another reads it
  // TODO: 50/50 chance I'll need to be able to dirty this cache if the proxy fails
  public synchronized Collection<RemotePeer> getNeighbors() {
    final List<Node> kadNodes = this.kbrNode.getNeighbours();

    // To prune old nodes in one pass, update the whole cache every time keeping old values.
    final Map<Node, RemotePeer> newCache = new HashMap<>();
    for (final Node kadNode : kadNodes) {
      RemotePeer rmiRef = this.neighborsCache.get(kadNode);
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


  public RemotePeer findPeer(final PeerIdentifier identifier) throws PeerNotFoundException {
    final Key key = this.kbrNode.getKeyFactory().create(identifier.toString());
    final List<il.technion.ewolf.kbr.Node> kadNodes = this.kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      throw new PeerNotFoundException(identifier);
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


  public PeerIdentifier getIdentifier() {
    return this.localIdentifier;
  }

  public RemotePeer getProxy() {
    return this.nodeProxy;
  }

  private RemotePeer importProxyTo(final il.technion.ewolf.kbr.Node node) {
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
      return (RemotePeer) ((ResponseMessage) resp).getContent();

    } catch (final Exception e) {

      // TODO: Logging
      e.printStackTrace();
      return null;
    }
  }
}
