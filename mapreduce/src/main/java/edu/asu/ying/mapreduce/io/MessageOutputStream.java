package edu.asu.ying.mapreduce.io;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.IOException;

/**
 * Writes {@link edu.asu.ying.mapreduce.messaging.Message} objects to an underlying {@link java.io.OutputStream}.
 */
public interface MessageOutputStream
{
	/**
	 * Writes the message to the stream and returns the number of messages written.
	 * </p>
	 * The number of messages written will be between one and the number set by the {@code replication} property of the
	 * message.
	 * @param message the message to write.
	 * @return the number of messages written - between one and the message's {@code replication} property value.
	 * @throws IOException if writing to the stream throws an exception.
	 */
	int write(final Message message) throws IOException;
}
