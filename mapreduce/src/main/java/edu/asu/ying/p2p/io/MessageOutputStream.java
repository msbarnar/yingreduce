package edu.asu.ying.p2p.io;

import com.google.common.util.concurrent.ListenableFutureTask;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Future;

import edu.asu.ying.p2p.io.message.Message;
import il.technion.ewolf.kbr.Node;

/**
 * Writes {@link edu.asu.ying.p2p.io.message.Message} objects to an underlying {@link
 * java.io.OutputStream}.
 */
public interface MessageOutputStream {

  /**
   * Synchronously writes the message to the stream, blocking until the message is written.
   *
   * @param message the message to write.
   */
  void write(final Message message) throws IOException;

  /**
   * Asynchronously writes the message to the stream, returning immediately.
   *
   * @param message the message to write.
   * @return a {@link ListenableFutureTask} providing the value {@code true} when complete.
   */
  ListenableFutureTask<Boolean> writeAsync(final Message message) throws IOException;

  Future<Serializable> writeAsyncRequest(final Message request) throws IOException;

  Future<Serializable> writeAsyncRequest(final Node node, final Message request) throws IOException;
}
