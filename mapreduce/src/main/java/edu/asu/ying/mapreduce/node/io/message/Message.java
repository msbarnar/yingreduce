package edu.asu.ying.mapreduce.node.io.message;

import java.io.Serializable;

import edu.asu.ying.mapreduce.common.HasProperties;
import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.p2p.NodeIdentifier;


/**
 * {@link Message} objects carry state information and requests for resource or connection
 * establishment around the network.
 */
public interface Message
    extends Serializable, HasProperties {

  /**
   * The message's ID is a universally unique identifier used to link received responses to their
   * previously sent request counterparts.
   *
   * @return a universally unique identifier
   */
  String getId();

  String getTag();

  NodeIdentifier getSourceNode();

  void setSourceNode(NodeIdentifier uri);

  NodeIdentifier getDestinationNode();

  /**
   * Makes this message into a response to {@code request}.
   *
   * @param request the message to which to respond.
   * @return this message.
   */
  Message makeResponseTo(Message request);
}
