package edu.asu.ying.mapreduce.messaging;

import edu.asu.ying.mapreduce.Properties;
import edu.asu.ying.mapreduce.rmi.resource.ResourceIdentifier;

import java.io.Serializable;
import java.util.Map;


/**
 * {@link Message} objects carry state information and requests for resources or connection establishment around the
 * network.
 */
public interface Message
	extends Serializable
{
	Properties getProperties();

	/**
	 * The message's ID is a universally unique identifier used to link received responses to their previously
	 * sent request counterparts.
	 * @return a universally unique identifier
	 */
	String getId();

	ResourceIdentifier getSourceUri();
	void setSourceUri(final ResourceIdentifier uri);

	ResourceIdentifier getDestinationUri();

	/**
	 * Replication allows a message to be delivered to at moest k hosts that are matched on its URI.
	 * @return the maximum number of hosts to which the message should be replicated.
	 */
	int getReplication();
}
