package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.table.TableID;

public final class PageOutResponse
	extends ResponseMessage
	implements TableMessage
{
	public PageOutResponse(final PageOutRequest request, final boolean success) {
		super(request);
		this.setStatus(success);
	}

	private static final long serialVersionUID = 5923013357695227098L;

	@Override
	public TableID getTableId() {
		return ((PageOutRequest) this.getRequest()).getTableId();
	}
}
