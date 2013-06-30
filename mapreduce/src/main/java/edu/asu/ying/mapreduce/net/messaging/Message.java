package edu.asu.ying.mapreduce.net.messaging;

import java.io.Serializable;
import java.net.URISyntaxException;

import edu.asu.ying.mapreduce.common.Properties;
import edu.asu.ying.mapreduce.net.resources.ResourceIdentifier;


/**
 * {@link Message} objects carry state information and requests for resources or connection
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

  ResourceIdentifier getSourceUri();

  void setSourceUri(final ResourceIdentifier uri);

  void setSourceUri(final String uri) throws URISyntaxException;

  ResourceIdentifier getDestinationUri();

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
  Message makeResponseTo(final Message request);
}
