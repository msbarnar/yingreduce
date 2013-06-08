package edu.asu.ying.mapreduce.rpc.messaging;

import java.io.Serializable;

import edu.asu.ying.mapreduce.rpc.net.Distributable;


/**
 * {@link Message} objects are the basic unit passed through sink chains while passing
 * into or out of the application domain, e.g. from disk to network.
 */
public interface Message
	extends Serializable, Distributable
{
	/**
	 * Get the transport headers associated with the message.
	 */
	public MessageProperties getProperties();
}
