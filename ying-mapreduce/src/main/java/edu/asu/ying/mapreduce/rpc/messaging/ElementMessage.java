package edu.asu.ying.mapreduce.rpc.messaging;

import java.io.Serializable;

import edu.asu.ying.mapreduce.rpc.net.NetworkKey;
import edu.asu.ying.mapreduce.table.*;

/**
 * {@link ElementMessage} objects carry requests for elements from a {@link ServerTableProxy}
 * to a data source and carry the {@link Element} back to the requesting proxy.
 * <p>
 * This enables {@link ServerTableProxy} to transparently link a {@link ServerTable}
 * to any data source that responds to requests for elements.
 */
public final class ElementMessage
	extends MessageBase
	implements TableMessage
{
	public enum Action {
		Get,
		Put
	}
	
	private static final long serialVersionUID = -8673199655323103844L;

	public ElementMessage(final TableID tableId, final Serializable key) {
		this(tableId, key, null);
		this.setAction(Action.Get);
	}
	public ElementMessage(final TableID tableId, final Serializable key, final Serializable value) {
		super(new NetworkKey().add(tableId.getId()).add(key));
		this.setTableId(tableId);
		this.setElement(new Element(key, value));
		this.setNetworkKey(this.makeNetworkKey());
		this.setAction(Action.Put);
	}
	public ElementMessage(final TableID tableId, final Element element) {
		this(tableId, element.getKey(), element.getValue());
	}
	
	protected final NetworkKey makeNetworkKey() {
		return new NetworkKey().add(this.getTableId().getId()).add(this.getElement().getKey());
	}
	
	protected final void setAction(final Action action) {
		this.properties.put("action", action);
	}
	public final Action getAction(final Action action) {
		return (Action) this.properties.get("action");
	}
	
	protected final void setElement(final Element element) {
		this.properties.put("element", element);
	}
	public final Element getElement() {
		return (Element) this.properties.get("element");
	}

	protected final void setTableId(final TableID tableId) {
		this.properties.put("table-id", tableId);
	}
	@Override
	public final TableID getTableId() {
		return (TableID) this.properties.get("table-id");
	}
}
