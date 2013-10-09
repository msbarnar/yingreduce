package edu.asu.ying.p2p.io.message;

import java.io.Serializable;

import edu.asu.ying.p2p.PeerIdentifier;


/**
 * {@link Message} objects carry state information and requests for resource or connection
 * establishment around the network.
 */
public interface Message
    extends Serializable {

  /**
   * The message's ID is a universally unique identifier used to link received responses to their
   * requests
   */
  String getId();

  String getTag();

  PeerIdentifier getSender();

  void setSender(PeerIdentifier peerId);

  PeerIdentifier getDestination();
}
