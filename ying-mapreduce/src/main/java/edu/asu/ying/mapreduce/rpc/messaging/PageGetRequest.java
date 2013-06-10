package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.rpc.net.NetworkKey;
import edu.asu.ying.mapreduce.table.TableID;

public final class PageGetRequest 
	extends MessageBase 
	implements TableMessage {

	private static final long serialVersionUID = 8513870105799255187L;

	private final TableID tableId;
	private final int pageIndex;
	
	public PageGetRequest(final TableID tableId, final int pageIndex) {
		super(new NetworkKey().add(tableId).add(pageIndex));
		this.tableId = tableId;
		this.pageIndex = pageIndex;
	}
	
	@Override
	public final TableID getTableId() { return this.tableId; }

	public final int getPageIndex() { return this.pageIndex; }
}
