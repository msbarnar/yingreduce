package edu.asu.ying.mapreduce.table;

public interface ServerTableProxyProvider
{
	public ServerTableProxy getProxy(final TableID tableId);
}
