package edu.asu.ying.mapreduce.io;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.asu.ying.mapreduce.net.messaging.Message;

/**
 * {@code Messenger} abstracts message and request sending and asynchronous response reception.
 */
public interface Messenger {

  /**
   * Synchronously sends a message with no expectation of a response.
   * @param message the message to be sent.
   */
  void sendMessage(final Message message) throws IOException;

  /**
   * Synchronously sends a message, returning a {@link ListenableFuture} promise of an asynchronous
   * response.
   * @param request the request message to be sent.
   * @param responseType the class of the response expected.
   * @param <T> the type of the request message.
   * @param <V> the type of the response message.
   * @return a {@link ListenableFuture} parameterized on the class of {@code responseType} promising
   * a future response of that type.
   */
  <T extends Message, V extends Message>
  ListenableFuture<T> sendRequestAsync(final V request, final Class<T> responseType)
      throws IOException;

  /**
   * Synchronously sends a message and blocks, waiting for a response.
   * @param request the request message to be sent.
   * @param responseType the class of the response expected.
   * @param <T> the type of the request message.
   * @param <V> the type of the response message.
   * @return a {@link ListenableFuture} parameterized on the class of {@code responseType} promising
   * a future response of that type.
   */
  <T extends Message, V extends Message>
  T sendRequest(final V request, final Class<T> responseType)
      throws IOException, ExecutionException, InterruptedException;
}
