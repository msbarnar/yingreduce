package edu.asu.ying.mapreduce.io.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.asu.ying.mapreduce.rpc.messaging.ExceptionMessage;
import edu.asu.ying.mapreduce.rpc.messaging.Message;
import edu.asu.ying.mapreduce.rpc.messaging.MessageSink;
import edu.asu.ying.mapreduce.rpc.messaging.PageGetRequest;
import edu.asu.ying.mapreduce.rpc.messaging.PageGetResponse;
import edu.asu.ying.mapreduce.rpc.messaging.PageOutRequest;
import edu.asu.ying.mapreduce.rpc.net.InvalidKeyException;
import edu.asu.ying.mapreduce.table.Page;
import edu.asu.ying.mapreduce.table.ServerTableProxy;
import edu.asu.ying.mapreduce.table.TableID;

public final class SimpleServerTableProxy
	implements ServerTableProxy
{
	private final TableID tableId;
	
	public int totalPageCount = -1;
	
	private final Map<Integer, Page> pages = new HashMap<Integer, Page>();
	
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
		if (message instanceof PageOutRequest) {
			final Page page = ((PageOutRequest) message).getPage();
			this.pages.put(page.getIndex(), page);
			return null;
		} else if (message instanceof PageGetRequest) {
			final int pageIndex = ((PageGetRequest) message).getPageIndex();
			
			final Page page = this.pages.get(pageIndex);
			if (page == null) {
				return new ExceptionMessage(new IllegalArgumentException("Page not found"));
			}
			
			return new PageGetResponse(page);
		} else {
			return null;
		}
	}

	public final int getPageCount() { return this.pages.size(); }
	public final TableID getTableId() { return this.tableId; }
}
