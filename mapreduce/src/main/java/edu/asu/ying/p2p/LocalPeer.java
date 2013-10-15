package edu.asu.ying.p2p;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import edu.asu.ying.p2p.rmi.RemoteImportException;


/**
 * Provides an interface to the local node and its listening facilities.
 */
public interface LocalPeer {

  /**
   * Gets the unique network identifier for this peer.
   */
  PeerIdentifier getIdentifier();

  /**
   * Connects this peer to an existing network of peers via the address of a peer in the network.
   *
   * @param bootstrap the address of a peer in an existing network to join.
   */
  void join(URI bootstrap) throws IOException;

  /**
   * Gets the peers to which this peer is directly connected.
   */
  List<RemotePeer> getNeighbors();

  /**
   * Finds a peer on any network of which this peer is a part.
   *
   * @return a public, network accessible, reference to the remote peer.
   */
  RemotePeer findPeer(PeerIdentifier identifier) throws PeerNotFoundException,
                                                        RemoteImportException;

  RemotePeer findPeer(String identifier) throws PeerNotFoundException, RemoteImportException;

  /**
   * Finds up to {@code count} peers near {@code identifier} and imports references to them. If any
   * reference imports fail with {@link RemoteImportException}, they are quietly excluded from the
   * list.
   */
  List<RemotePeer> findPeers(PeerIdentifier identifier, int count);

  List<RemotePeer> findPeers(String identifier, int count);
}
