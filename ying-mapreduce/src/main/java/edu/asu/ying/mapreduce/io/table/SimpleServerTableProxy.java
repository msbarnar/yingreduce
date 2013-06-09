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
	
	public int totalPageCount = -1;
	
	private final List<Page> pages = new ArrayList<Page>();
	
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
		this.pages.add(msg.getPage());
		
		return null;
	}

	public final int getPageCount() { return this.pages.size(); }
	public final TableID getTableId() { return this.tableId; }
}
