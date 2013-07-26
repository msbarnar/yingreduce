package edu.asu.ying.mapreduce.net.io.kad;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import edu.asu.ying.mapreduce.common.concurrency.FilteredFutures;
import edu.asu.ying.mapreduce.common.event.FilteredValueEvent;
import edu.asu.ying.mapreduce.common.filter.Filter;
import edu.asu.ying.mapreduce.common.filter.FilterClass;
import edu.asu.ying.mapreduce.common.filter.FilterString;
import edu.asu.ying.mapreduce.net.io.Channel;
import edu.asu.ying.mapreduce.net.io.MessageOutputStream;
import edu.asu.ying.mapreduce.net.io.SendMessageStream;
import edu.asu.ying.mapreduce.net.messaging.FilterMessage;
import edu.asu.ying.mapreduce.net.messaging.Message;
import edu.asu.ying.mapreduce.net.messaging.MessageHandler;

/**
 * {@code KadChannel} encompasses the {@link MessageHandler} and {@link MessageOutputStream} tied
 * to the underlying Kademlia network. The {@code KadChannel} provides a single point of access for
 * input from and output to the network.
 */
@Singleton
public final class KadChannel implements Channel {

  private final MessageHandler incomingMessageHandler;
  private final MessageOutputStream sendStream;

  @Inject
  private KadChannel(final MessageHandler incomingMessageHandler,
                     final @SendMessageStream MessageOutputStream sendStream) {

    this.incomingMessageHandler = incomingMessageHandler;
    this.sendStream = sendStream;
  }

  @Override
  public MessageHandler getIncomingMessageHandler() {
    return this.incomingMessageHandler;
  }

  @Override
  public void sendMessage(final Message message) throws IOException {
    this.sendStream.write(message);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <TRequest extends Message, TResponse extends Message> ListenableFuture<TResponse>
  sendRequestAsync(final TRequest request, final Class<TResponse> responseType) throws IOException {

    // Get one response of type responseType matching request
    // Don't block on the response
    return FilteredFutures.<TResponse>getFrom(
        (FilteredValueEvent<TResponse>) this.incomingMessageHandler.getIncomingMessageEvent())
        .getOne(
            Filter.on.allOf(
                FilterClass.is(responseType),
                FilterMessage.id(FilterString.equalTo(request.getId()))
            )
        );
  }

  @Override
  public <T extends Message, V extends Message> T sendRequest(final V request,
                                                              final Class<T> responseType)
      throws IOException, ExecutionException, InterruptedException {

    return this.sendRequestAsync(request, responseType).get();
  }
}
