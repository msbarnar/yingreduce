package edu.asu.ying.mapreduce.rpc.messaging;

/**
 * Interface for a class that is capable of registering to handle messages from a {@link MessageDispatch}.
 */
public interface ReceiveMessageHandler
	extends MessageSink
{
	public void registerForMessages(final MessageDispatch dispatcher);
}
