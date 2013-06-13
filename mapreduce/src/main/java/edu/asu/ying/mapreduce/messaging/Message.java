package edu.asu.ying.mapreduce.messaging;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;


/**
 * {@link Message} objects carry information through {@link MessageSink} chains.
 */
public interface Message
{
	public Map<Serializable, Serializable> getProperties();
	public URI getDestinationUri();
}
