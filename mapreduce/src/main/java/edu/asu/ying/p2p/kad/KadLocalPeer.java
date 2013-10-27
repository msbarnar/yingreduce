package edu.asu.ying.p2p.kad;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.asu.ying.rmi.RemoteImportException;
import edu.asu.ying.p2p.Channel;
import edu.asu.ying.p2p.InvalidContentException;
import edu.asu.ying.p2p.LocalPeer;
import edu.asu.ying.p2p.PeerNotFoundException;
import edu.asu.ying.p2p.RemotePeer;
import edu.asu.ying.p2p.RemotePeerRequestHandler;
import edu.asu.ying.p2p.message.RequestMessage;
import edu.asu.ying.p2p.message.ResponseMessage;
import il.technion.ewolf.kbr.Key;
import il.technion.ewolf.kbr.KeybasedRouting;
import il.technion.ewolf.kbr.Node;


/**
 * {@code KadLocalPeer} implements the local high-level peer-to-peer behavior on top of the
 * Kademlia
 * routing system. </p> The local peer maintains the Kademlia node, the local job scheduler, and
 * the
 * local database interface.
 */
public final class KadLocalPeer implements LocalPeer {

  private static Logger log = Logger.getLogger(KadLocalPeer.class.getName());

  // Local kademlia node
  private final KeybasedRouting kbrNode;

  // Pipe to the kad network
  private final Channel networkChannel;

  // Getting RMI references to neighbors is expensive, so cache the reference every time we get one
  private Map<Node, RemotePeer> neighborsCache = new HashMap<>();

  @Inject
  private KadLocalPeer(KeybasedRouting kbrNode,
                       Channel channel,
                       // Depend on this just to get it bound and running
                       RemotePeerRequestHandler peerRequestHandler) throws ExportException {

    // The local Kademlia node for peer discovery
    this.kbrNode = kbrNode;
    // Connect the P2P messaging system to the Kademlia node
    this.networkChannel = channel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return kbrNode.getLocalNode().getKey().toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void join(URI bootstrap) throws IOException {
    try {
      List<URI> bootstrapUris = Arrays.asList(URI.create(bootstrap.toString()));
      kbrNode.join(bootstrapUris);

    } catch (IllegalStateException e) {
      throw new PeerNotFoundException(bootstrap);
    }
  }

  @Override
  public void close() {
    neighborsCache.clear();
    try {
      networkChannel.close();
    } catch (IOException ignored) {
    }
    kbrNode.shutdown();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized List<RemotePeer> getNeighbors() {
    List<Node> kadNodes = kbrNode.getNeighbours();

    // To prune old nodes in one pass, update the whole cache every time keeping old values.
    Map<Node, RemotePeer> newCache = new HashMap<>();
    for (Node kadNode : kadNodes) {
      RemotePeer rmiRef = neighborsCache.get(kadNode);
      // Update missing refs
      if (rmiRef == null) {
        try {
          rmiRef = importProxyTo(kadNode);
        } catch (RemoteException e) {
          log.log(Level.WARNING, "Exception importing proxy to remote node", e);
        }
      }
      if (rmiRef != null) {
        // Retain only nodes that are still connected
        newCache.put(kadNode, rmiRef);
      }
    }

    // Keep the cache up to date
    neighborsCache = newCache;

    return ImmutableList.copyOf(neighborsCache.values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public RemotePeer findPeer(String name)
      throws PeerNotFoundException, RemoteImportException {

    Key key = kbrNode.getKeyFactory().create(name);
    List<il.technion.ewolf.kbr.Node> kadNodes = kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      throw new PeerNotFoundException(name);
    }
    return this.importProxyTo(kadNodes.get(0));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<RemotePeer> findPeers(String name, int count) {
    Key key = kbrNode.getKeyFactory().create(name);
    List<il.technion.ewolf.kbr.Node> kadNodes = kbrNode.findNode(key);
    if (kadNodes.isEmpty()) {
      return Collections.emptyList();
    }
    List<RemotePeer> peers = new ArrayList<>(count);
    for (il.technion.ewolf.kbr.Node kadNode : kadNodes) {
      try {
        peers.add(importProxyTo(kadNode));
        if (peers.size() >= count) {
          break;
        }
      } catch (RemoteImportException e) {
        log.log(Level.WARNING, "Exception importing proxy to remote node", e);
      }
    }
    return peers;
  }

  /**
   * Given a Kademlia node, sends a request to the remote {@link RemotePeerRequestHandler} and
   * waits
   * for a response containing a {@link RemotePeer} proxy to that peer.
   */
  // TODO: Target for cleanup
  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  private RemotePeer importProxyTo(il.technion.ewolf.kbr.Node node) throws RemoteImportException {

    // Send the request to the request handler
    RequestMessage request = RemotePeerRequestHandler.createRequest();
    Future<Serializable> response;
    try {
      response = networkChannel.getMessageOutputStream().writeAsyncRequest(node, request);

    } catch (IOException e) {
      throw new RemoteImportException(e);
    }

    // Wait for the response and get the proxy
    try {
      Serializable resp = response.get();
      if (!(resp instanceof ResponseMessage)) {
        throw new InvalidContentException(ResponseMessage.class, resp);
      }
      ResponseMessage rm = ((ResponseMessage) resp);
      if (rm.getException() != null) {
        throw new RemoteImportException(rm.getException());
      }

      return (RemotePeer) ((ResponseMessage) resp).getContent();

    } catch (RemoteException | InvalidContentException | InterruptedException
        | ExecutionException e) {

      throw new RemoteImportException(e);
    }
  }
}
