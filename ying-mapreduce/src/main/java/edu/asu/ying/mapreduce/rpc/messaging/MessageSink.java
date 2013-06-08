package edu.asu.ying.mapreduce.rpc.messaging;


/**
 * Data moving into or out of the application domain, e.g. from disk to the network,
 * pass through a chain of {@link MessageSink} objects in the form of a {@link Message}.
 * Each {@link MessageSink} receives the message, performs an operation, and passes the
 * message to the next sink in the chain.
 */
public interface MessageSink
{
	/**
	 * Returns the next {@link MessageSink} in the chain.
	 */
	public MessageSink getNextMessageSink();
	/**
	 * Processes the given {@link Message} and returns a reply {@link Message}.
	 * @param message the message to process.
	 * @throw IOException
	 */
	public Message processMessage(final Message message);
	/* TODO: Asynchronous message processing
	 /**
	 *
	 * Processes the given {@link Message} asynchronously and returns a {@link Future}
	 * promise of a reply {@link Message}.
	 * @param message the message to process.
	 * @return a promise of a future reply message.
	 
	public Future<Message> asyncProcessMessage(final Message message);
	*/
}
