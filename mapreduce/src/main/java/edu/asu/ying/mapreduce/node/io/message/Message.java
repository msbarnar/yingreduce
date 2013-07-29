package edu.asu.ying.mapreduce.node.io.message;

import java.io.Serializable;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.node.NodeURI;


/**
 * {@link Message} objects carry state information and requests for resource or connection
 * establishment around the network.
 */
public interface Message
    extends Serializable {

  Properties getProperties();

  /**
   * The message's ID is a universally unique identifier used to link received responses to their
   * previously sent request counterparts.
   *
   * @return a universally unique identifier
   */
  String getId();

  String getTag();

  NodeURI getSourceNode();

  void setSourceNode(NodeURI uri);

  NodeURI getDestinationNode();

  /**
   * Replication allows a message to be delivered to at moest k hosts that are matched on its URI.
   *
   * @return the maximum number of hosts to which the message should be replicated.
   */
  int getReplication();

  /**
   * Makes this message into a response to {@code request}.
   *
   * @param request the message to which to respond.
   * @return this message.
   */
  Message makeResponseTo(Message request);
}
