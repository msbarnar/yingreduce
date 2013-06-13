package edu.asu.ying.mapreduce.messaging;

import java.io.IOException;

/**
 * A {@link MessageSink} receives {@link Message} objects and passes them to some destination.
 * <p>
 * The use of message sink chains allows room for extensibility and customization between layers of abstraction.
 */
public interface MessageSink
{
	public MessageSink getNextSink();
	public Message accept(final Message message) throws IOException;
}
