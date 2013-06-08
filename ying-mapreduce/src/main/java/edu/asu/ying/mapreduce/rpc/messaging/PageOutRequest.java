package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.table.Page;
import edu.asu.ying.mapreduce.table.TableID;

/**
 * Packages a {@link Page} for transmission across the network
 * <p>
 * The network key property of the {@link Message} is set using {@link Page#getNetworkKey}.
 */
public final class PageOutRequest
	extends MessageBase
	implements TableMessage
{
	private static final long serialVersionUID = 6574267634324164998L;

	public PageOutRequest(final Page page) {
		super(page.getNetworkKey());
		this.setPage(page);
		this.setTableId(page.getTableId());
	}
	
	private void setPage(final Page page) { 
		this.properties.put("page", page); 
	}
	public Page getPage() { 
		return (Page) this.properties.get("page");
	}

	private void setTableId(TableID id) { 
		this.properties.put("table-id", id); 
	}
	@Override
	public TableID getTableId() { 
		return (TableID) this.properties.get("table-id"); 
	}
}
