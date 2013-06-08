package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.table.TableID;

/**
 * Base class for a message regarding a specific table.
 */
public interface TableMessage
	extends Message
{
	public TableID getTableId();
}
