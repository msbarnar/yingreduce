package edu.asu.ying.mapreduce.rpc.channels;

import java.io.Serializable;
import java.util.Map;

/**
 * Base interface for a channel that transmits data.
 */
public interface Channel
{
	/**
	 * Returns the map of {@link Writable} properties for the {@link ChannelSink}.
	 */
	public Map<? extends Serializable, ? extends Serializable> getProperties();
	
	public void close();
}
