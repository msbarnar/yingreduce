package edu.asu.ying.mapreduce.io.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.ying.mapreduce.rpc.messaging.Message;
import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;
import edu.asu.ying.mapreduce.rpc.messaging.PageOutRequest;
import edu.asu.ying.mapreduce.table.Page;
import edu.asu.ying.mapreduce.table.ServerTableProxy;
import edu.asu.ying.mapreduce.table.TableID;

public final class SimpleServerTableProxy
	implements ServerTableProxy
{
	private final TableID tableId;
	
	private final Map<String, List<Page>> tables = new HashMap<String, List<Page>>();
	
	public SimpleServerTableProxy(final TableID tableId) {
		this.tableId = tableId;
	}
	
	@Override
	public MessageSink getNextMessageSink() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message processMessage(final Message message) {
		final PageOutRequest msg = (PageOutRequest) message;
		final String tableId = msg.getTableId().toString();
		List<Page> pages = this.tables.get(tableId);
		if (pages == null) {
			pages = new ArrayList<Page>();
			this.tables.put(tableId, pages);
		}
		pages.add(msg.getPage());
		
		return null;
	}

	public final TableID getTableId() { return this.tableId; }
}
