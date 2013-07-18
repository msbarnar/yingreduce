package edu.asu.ying.mapreduce.io;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.asu.ying.mapreduce.common.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.common.filter.FilterString;
import edu.asu.ying.mapreduce.net.messaging.FilterMessage;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;

/**
 * {@code MessengerImpl} is the base implementation of {@link Messenger} and handles synchronous
 * message and request sending and asynchronous response reception.
 */
public class MessengerImpl implements Messenger {

  private final MessageOutputStream sendStream;
  private final FilteredValueEvent<Message> onIncomingMessage;

  @Inject
  private MessengerImpl(final @SendMessageStream MessageOutputStream sendStream,
                        final MessageHandler incomingMessageHandler) {

    this.sendStream = sendStream;
    this.onIncomingMessage = incomingMessageHandler.getIncomingMessageEvent();
  }

  @Override
  public void sendMessage(final Message message) throws IOException {
    this.sendStream.write(message);
  }

  @Override
  public <T extends Message, V extends Message> ListenableFuture<T> sendRequestAsync(
      final V request, final Class<T> responseType) throws IOException {

    // Get one response of type responseType matching request
    final ListenableFuture<T> response = FilteredFutures.<T>getFrom(this.onIncomingMessage)
        .getOne(
            Filter.on.allOf(
                FilterClass.is(responseType),
                FilterMessage.id(FilterString.equalTo(request.getId()))
            )
        );

    // Don't block on the response
    return response;
  }

  @Override
  public <T extends Message, V extends Message> T sendRequest(final V request,
                                                              final Class<T> responseType)
    throws IOException, ExecutionException, InterruptedException {

    return this.sendRequestAsync(request, responseType).get();
  }
}
