package edu.asu.ying.mapreduce.io.table;

import edu.asu.ying.mapreduce.table.ServerTableProxy;
import edu.asu.ying.mapreduce.table.ServerTableProxyProvider;
import edu.asu.ying.mapreduce.table.TableID;

/**
 * Acts as the data proxy between a {@link ServerTable} and the filesystem.
 * <p>
 * Tables are represented as directories and pages as individual files.
 */
public final class SimpleServerTableProxyProvider
	implements ServerTableProxyProvider
{
	public SimpleServerTableProxyProvider() {
	}

	@Override
	public ServerTableProxy getProxy(TableID tableId) {
		return new SimpleServerTableProxy(tableId);
	}
}
