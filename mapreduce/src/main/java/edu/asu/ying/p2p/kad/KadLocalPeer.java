package edu.asu.ying.p2p.kad;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import edu.asu.ying.database.page.ServerPageSink;
import edu.asu.ying.mapreduce.mapreduce.scheduling.LocalScheduler;
import edu.asu.ying.mapreduce.mapreduce.scheduling.SchedulerImpl;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerIdentifier;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.net.Channel;
import edu.asu.ying.p2p.net.InvalidContentException;
import edu.asu.ying.p2p.net.message.RequestMessage;
import edu.asu.ying.p2p.net.message.ResponseMessage;
import edu.asu.ying.p2p.rmi.AbstractExportable;
import edu.asu.ying.p2p.rmi.RMIActivator;
import edu.asu.ying.p2p.rmi.RMIActivatorImpl;
import edu.asu.ying.p2p.rmi.RMIRequestHandler;
import edu.asu.ying.p2p.rmi.RemoteImportException;
import edu.asu.ying.p2p.rmi.RemotePageSinkProxy;
import edu.asu.ying.p2p.rmi.RemotePeerProxy;
import edu.asu.ying.p2p.rmi.RemoteSchedulerProxy;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@code KadLocalPeer} implements the local high-level peer-to-peer behavior on top of the Kademlia
 * routing system. </p> The local peer maintains the Kademlia node, the local job scheduler, and the
 * local database interface.
 */
public final class KadLocalPeer extends AbstractExportable<RemotePeer> implements LocalPeer {

  // Local kademlia node
  private final KeybasedRouting kbrNode;
  private final PeerIdentifier localIdentifier;

  // Pipe to the kad network
  private final Channel networkChannel;

  // Getting RMI references to neighbors is expensive, so cache the reference every time we get one
  private Map<Node, RemotePeer> neighborsCache = new HashMap<>();

  // Manages RMI export of objects for access by remote peers.
  private final RMIActivator activator;

  // Schedules mapreduce jobs and tasks
  private final LocalScheduler scheduler;

  // Accepts pages from the network
  private final ServerPageSink pageSink;


  public KadLocalPeer(final int port) throws InstantiationException {

    // The local Kademlia node for node discovery
    this.kbrNode = KademliaNetwork.createNode(port);

    // FIXME: SEVERE: The key is chosen using the local interface address which is always localhost
    this.localIdentifier =
        new KadPeerIdentifier(this.kbrNode.getLocalNode().getKey());

    this.networkChannel = KademliaNetwork.createChannel(this.kbrNode);

    // Start the remote reference provider
    this.activator = new RMIActivatorImpl();

    // Start the scheduler
    this.scheduler = new SchedulerImpl(this);
    try {
      ((SchedulerImpl) this.scheduler).export(RemoteSchedulerProxy.class, this.activator);
    } catch (final ExportException e) {
      // TODO: Logging
      throw new InstantiationException("Failed to export server scheduler");
    }

    this.scheduler.start();

    // Start the interface for sinking incoming pages
    this.pageSink = new ServerPageSink();
    try {
      this.pageSink.export(RemotePageSinkProxy.class, this.activator);
    } catch (final ExportException e) {
      // TODO: Logging
      throw new InstantiationException("Failed to export server page sink");
    }

    // Allow peers to access the node and scheduler remotely.
    try {
      this.export(RemotePeerProxy.class, this.activator);
    } catch (final ExportException e) {
      // TODO: Logging
      throw new InstantiationException("Failed to export server peer");
    }

    // Allow peers to discover this node's RMI interfaces.
    RMIRequestHandler.exportNodeToChannel(this, networkChannel);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void join(final URI bootstrap) throws IOException {
    try {
      final List<URI> bootstrapUris = Arrays.asList(URI.create(bootstrap.toString()));
      this.kbrNode.join(bootstrapUris);

    } catch (final IllegalStateException e) {
      throw new PeerNotFoundException(bootstrap);
    }
  }


  /**
   * {@inheritDoc}
   */
  // TODO: 50/50 chance I'll need to be able to dirty this cache if the proxy fails
  // TODO: I don't know what the above comment means anymore, but it looks important
  @Override
  public synchronized List<RemotePeer> getNeighbors() {
    final List<Node> kadNodes = this.kbrNode.getNeighbours();

    // To prune old nodes in one pass, update the whole cache every time keeping old values.
    final Map<Node, RemotePeer> newCache = new HashMap<>();
    for (final Node kadNode : kadNodes) {
      RemotePeer rmiRef = this.neighborsCache.get(kadNode);
      // Update missing refs
      if (rmiRef == null) {
        try {
          rmiRef = this.importProxyTo(kadNode);
        } catch (final RemoteException e) {
          // TODO: Logging
          e.printStackTrace();
        }
      }
      if (rmiRef != null) {
        // Retain only nodes that are still connected
        newCache.put(kadNode, rmiRef);
      }
    }

    // Keep the cache up to date
    this.neighborsCache = newCache;

    return ImmutableList.copyOf(this.neighborsCache.values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemotePeer findPeer(final PeerIdentifier identifier)
      throws PeerNotFoundException, RemoteImportException {

    final Key key = this.kbrNode.getKeyFactory().create(identifier.toString());
    final List<il.technion.ewolf.kbr.Node> kadNodes = this.kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      throw new PeerNotFoundException(identifier);
    }
    return this.importProxyTo(kadNodes.get(0));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<RemotePeer> findPeers(final PeerIdentifier identifier, final int count) {

    final Key key = this.kbrNode.getKeyFactory().create(identifier.toString());
    final List<il.technion.ewolf.kbr.Node> kadNodes = this.kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      return Collections.emptyList();
    }
    final List<RemotePeer> peers = new ArrayList<>();
    for (final il.technion.ewolf.kbr.Node kadNode : kadNodes) {
      try {
        peers.add(this.importProxyTo(kadNode));
      } catch (final RemoteImportException e) {
        // TODO: Logging
        e.printStackTrace();
      }
    }
    return peers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ServerPageSink getPageSink() {
    return this.pageSink;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LocalScheduler getScheduler() {
    return this.scheduler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RMIActivator getActivator() {
    return this.activator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PeerIdentifier getIdentifier() {
    return this.localIdentifier;
  }

  /**
   * Given a Kademlia node, sends a request to the remote {@link RMIRequestHandler} and waits for a
   * response containing a {@link java.rmi.Remote} proxy to the {@link RemotePeer} on that node.
   */
  // TODO: Target for cleanup
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private RemotePeer importProxyTo(final il.technion.ewolf.kbr.Node node) throws
                                                                          RemoteImportException {
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
        throw new InvalidContentException(ResponseMessage.class, resp);
      }
      final ResponseMessage rm = ((ResponseMessage) resp);
      if (rm.getException() != null) {
        throw new RemoteImportException(rm.getException());
      }

      return (RemotePeer) ((ResponseMessage) resp).getContent();

    } catch (final RemoteException | InvalidContentException | InterruptedException
        | ExecutionException e) {

      // TODO: Logging
      throw new RemoteImportException(e);
    }
  }
}
