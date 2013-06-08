package edu.asu.ying.mapreduce.rpc.messaging;

import edu.asu.ying.mapreduce.rpc.net.NetworkKey;


/**
 * Provides a base implementation of the {@link Message} class which specifies the following properties:
 *  - the fully qualified type name of the message object and
 *  - an optional network key that provides routing information for the message
 */
public abstract class MessageBase
	implements Message
{
	private static final long serialVersionUID = 108599904922354370L;
	
	protected final MessageProperties properties = new MessageProperties();
	
	/**
	 * Initialize the message for local communication.
	 * <p>
	 * Unless a network key is set, this message will have no ability to be routed
	 * in the network.
	 * @param type
	 */
	protected MessageBase() {
	}
	/**
	 * Initialize the message with a network key.
	 * <p>
	 * The network key determines the destination nodes in the network and should be
	 * set to dictate the proper routing of the contents.
	 * <p>
	 * E.g. for a message requesting a piece of data, the network key should be set
	 * using the appropriate identifier of that data such that the key routes to the
	 * node holding the data.
	 * 
	 * @param type the type of the deriving class, used on the receiving end for
	 * interpreting the message.
	 * @param networkKey The network key used to route the message.
	 */
	protected MessageBase(final NetworkKey networkKey) {
		this.setNetworkKey(networkKey);
	}
	
	@Override
	public MessageProperties getProperties() { return this.properties; }
	
	protected void setNetworkKey(final NetworkKey networkKey) {
		this.properties.put("__network-key__", networkKey);
	}
	/**
	 * Returns the network key associated with this message.
	 * <p>
	 * The network key determines the destination nodes in the network.
	 */
	@Override
	public NetworkKey getNetworkKey() {
		return (NetworkKey) this.properties.get("__network-key__");
	}
	
	/**
	 * Messages are all equal if their properties are equal.
	 */
	@Override
	public boolean equals(final Object rhs) {
		if (rhs == this)
			return true;
		if (rhs == null || !(rhs instanceof MessageBase))
			return false;
		
		return this.properties.equals(((MessageBase)rhs).getProperties());
	}
	
	@Override
	public int hashCode() {
		return this.properties.hashCode();
	}
}
