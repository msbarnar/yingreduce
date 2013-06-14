package edu.asu.ying.mapreduce.messaging.io;

import edu.asu.ying.mapreduce.messaging.Message;

import java.io.IOException;

/**
 * Writes {@link edu.asu.ying.mapreduce.messaging.Message} objects to an underlying {@link java.io.OutputStream}.
 */
public interface MessageOutputStream
{
	public void write(final Message message) throws IOException;
}
