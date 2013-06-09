package edu.asu.ying.mapreduce.io.table;

import java.util.HashMap;
import java.util.Map;

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
	private final Map<String, SimpleServerTableProxy> tables = new HashMap<String, SimpleServerTableProxy>();
	
	public SimpleServerTableProxyProvider() {
	}

	@Override
	public ServerTableProxy getProxy(TableID tableId) {
		final SimpleServerTableProxy table = new SimpleServerTableProxy(tableId);
		this.tables.put(tableId.toString(), table);
		return table;
	}
	
	public final Map<String, SimpleServerTableProxy> getTable() { return this.tables; }
}
