package edu.asu.ying.mapreduce.table;

import edu.asu.ying.mapreduce.rpc.messaging.*;

/**
 * {@link ServerTableProxy} provides transparent access to the actual data referenced by
 * {@link ServerTable}.
 * <p>
 * The proxy class serves elements by making requests on a {@link MessageSink} chain and
 * returning the result as {@link Element} objects. The actual data source will implement
 * {@link MessageSink} and will respond to {@link ElementMessage}.
 */
public interface ServerTableProxy
	extends MessageSink
{
	public TableID getTableId();
}