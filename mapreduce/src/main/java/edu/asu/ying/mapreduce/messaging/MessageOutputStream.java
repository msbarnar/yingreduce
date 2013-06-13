package edu.asu.ying.mapreduce.messaging;

import java.io.OutputStream;

/**
 * Writes {@link Message} objects to an underlying {@link OutputStream}.
 */
public interface MessageOutputStream
{
	public void write(final Message message);
}
