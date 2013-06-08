package edu.asu.ying.mapreduce.io.table;

import edu.asu.ying.mapreduce.rpc.messaging.Message;
import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;
import edu.asu.ying.mapreduce.table.ServerTableProxy;
import edu.asu.ying.mapreduce.table.TableID;

public final class SimpleServerTableProxy
	implements ServerTableProxy
{
	private final TableID tableId;
	
	public SimpleServerTableProxy(final TableID tableId) {
		this.tableId = tableId;
	}
	
	@Override
	public MessageSink getNextMessageSink() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message processMessage(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	public final TableID getTableId() { return this.tableId; }
}
