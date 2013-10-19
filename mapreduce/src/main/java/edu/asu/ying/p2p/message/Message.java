package edu.asu.ying.p2p.message;

import java.io.Serializable;


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

  String getSender();

  void setSender(String peerId);

  String getDestination();
}
