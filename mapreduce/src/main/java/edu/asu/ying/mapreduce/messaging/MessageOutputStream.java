package edu.asu.ying.mapreduce.messaging;

import java.io.IOException;

/**
 * Writes {@link Message} objects to an underlying {@link java.io.OutputStream}.
 */
public interface MessageOutputStream
{
	public void write(final Message message) throws IOException;
}
