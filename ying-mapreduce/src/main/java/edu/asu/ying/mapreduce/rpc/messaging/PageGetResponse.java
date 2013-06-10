package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.table.Page;
import edu.asu.ying.mapreduce.table.TableID;

public class PageGetResponse 
	extends MessageBase 
	implements TableMessage 
{
	
	private static final long serialVersionUID = -4853820128295682156L;

	private final Page page;
	
	public PageGetResponse(final Page page) {
		this.page = page;
	}
	
	public final int getPageIndex() { return this.page.getIndex(); }
	public final Page getPage() { return this.page; }
	
	@Override
	public TableID getTableId() { return this.page.getTableId(); }
}
