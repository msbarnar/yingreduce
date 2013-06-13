package edu.asu.ying.mapreduce.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.UUID;


/**
 * {@link Message} objects carry state information and requests for resources or connection establishment around the
 * network.
 */
public interface Message
	extends Serializable
{
	public Map<Serializable, Serializable> getProperties();

	/**
	 * The message's ID is a universally unique identifier used to link received responses to their previously
	 * sent request counterparts.
	 * @return a universally unique identifier
	 */
	public String getId();

	public URI getSourceUri();
	public void setSourceUri(final URI uri);

	public URI getDestinationUri();

	/**
	 * Replication allows a message to be delivered to at moest k hosts that are matched by its URI.
	 * @return the maximum number of hosts to which the message should be replicated.
	 */
	public int getReplication();
}
