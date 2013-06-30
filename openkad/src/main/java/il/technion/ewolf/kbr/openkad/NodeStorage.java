package il.technion.ewolf.kbr.openkad;

import il.technion.ewolf.kbr.Node;

import java.util.List;


public interface NodeStorage {
	/**
	 * Register this table structure to listen to incoming messages and update itself
	 * accordingly.
	 * Invoke this method after creating the entire system
	 */
	public void registerIncomingMessageHandler();
	/**
	 * 
	 * @return a list containing all the nodes in the table structure
	 */
	public List<Node> getAllNodes();

}
