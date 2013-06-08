package edu.asu.ying.mapreduce.table;

import java.io.IOException;

import edu.asu.ying.mapreduce.rpc.messaging.*;

/**
 * {@link ServerTable} is the heart of the P2P database on the Server (storage and processing) side.
 * <p>
 * Pages of key->value elements are received from the network as distributed by a Client.
 * As elements are received, they are delegated to a data proxy which stores them.
 * <p>
 * {@link ServerTable} accept a {@link Mappable} class for data processing.
 * Elements are retreived from the data proxy and passed to {@link Mappable#map} one-by-one.
 * The result from {@link Mappable#map} is routed to the reducer associated with this task.
 * 
 * @see Page
 * @see Element
 */
public final class ServerTable
	implements MessageSink
{
	private final TableID id;
	private final ServerTableProxy dataProxy;
	
	public ServerTable(final TableID id, final ServerTableProxy dataProxy) {
		this.id = id;
		this.dataProxy = dataProxy;
	}

	public final TableID getTableId() { return this.id; }
	
	/*****************************************************************
	 * MessageSink implementation									 */
	public MessageSink getNextMessageSink() {
		return this.dataProxy;
	}

	/**
	 * We'll be getting {@link PageOutRequest} from the network so pass those
	 * to the data proxy for storage.
	 * 
	 * @return A response message if the page was stored, null if the table will not
	 * accept this page, or the original message if the table cannot handle this message.
	 * @throws IOException 
	 */
	@Override
	public Message processMessage(final Message message) {
		if (message instanceof TableMessage) {
			// If this isn't our page, signal that upstream by not responding with anything.
			if (!((TableMessage) message).getTableId().equals(this.id)) {
				return null;
			}
			return this.dataProxy.processMessage(message);
	
		} else {
			return message;
		}
	}
}
