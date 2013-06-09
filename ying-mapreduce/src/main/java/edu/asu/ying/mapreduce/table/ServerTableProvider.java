package edu.asu.ying.mapreduce.table;

import java.util.*;

import edu.asu.ying.mapreduce.rpc.messaging.*;

/**
 * Receives {@link PageOutMessage} requests from the network and spawns
 * instances of {@link ServerTable} to handle them.
 */
public final class ServerTableProvider
	implements ReceiveMessageHandler
{
	private final Map<TableID, ServerTable> tables = 
			new HashMap<TableID, ServerTable>();
	
	private final ServerTableProxyProvider tableProxyProvider;
	
	public ServerTableProvider(final ServerTableProxyProvider tableProxyProvider) {
		this.tableProxyProvider = tableProxyProvider;
	}

	public final ServerTable getTable(final TableID tableId) {
		return new ServerTable(tableId, this.tableProxyProvider.getProxy(tableId));
	}
	
	/*****************************************************************
	 * MessageSink implementation									 */
	@Override
	public MessageSink getNextMessageSink() {
		return null;
	}

	@Override
	public Message processMessage(final Message message) {
		final TableMessage request = (TableMessage) message;
		// Get the TableID and check if we already have that table open
		final TableID tableId = request.getTableId();
		ServerTable table = this.tables.get(tableId);
		if (table == null) {
			// Spawn a new table
			table = this.getTable(tableId);
			this.tables.put(tableId, table);
		}
		return table.processMessage(message);
	}

	/*****************************************************************
	 * ServerMessageHandler implementation							 */
	/**
	 * Register to handle messages at the visiting {@link MessageDispatch}.
	 */
	@Override
	public void registerForMessages(final MessageDispatch dispatcher) {
		dispatcher.registerSink(PageOutRequest.class, this);
	}
}
